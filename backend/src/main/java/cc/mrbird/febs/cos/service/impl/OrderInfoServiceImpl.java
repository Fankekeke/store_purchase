package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.OutStockRecordMapper;
import cc.mrbird.febs.cos.dao.StorageRecordMapper;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.dao.OrderInfoMapper;
import cc.mrbird.febs.cos.service.IOrderInfoService;
import cc.mrbird.febs.cos.service.IOutStockRecordService;
import cc.mrbird.febs.cos.service.IProductTypeInfoService;
import cc.mrbird.febs.cos.service.IStorehouseInfoService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    private final IStorehouseInfoService storehouseInfoService;

    private final StorageRecordMapper storageRecordMapper;

    private final OutStockRecordMapper outStockRecordMapper;

    private final IProductTypeInfoService productTypeInfoService;

    /**
     * 分页查询订单信息
     *
     * @param page      分页对象
     * @param orderInfo 订单信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectOrderPage(Page<OrderInfo> page, OrderInfo orderInfo) {
        return baseMapper.selectOrderPage(page, orderInfo);
    }

    /**
     * 订单详情导出
     *
     * @param code 订单编号
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public LinkedHashMap<String, Object> export(String code) throws Exception {
        if (StrUtil.isEmpty(code)) {
            throw new FebsException("订单编号不能为空！");
        }
        List<LinkedHashMap<String, Object>> materialMapList = baseMapper.orderDetail(code);
        if (CollectionUtil.isEmpty(materialMapList)) {
            throw new FebsException(("订单信息为空！"));
        }
        List<StorehouseInfo> materialList = JSONUtil.toList(JSONUtil.toJsonStr(materialMapList), StorehouseInfo.class);
        BigDecimal totalPrice = materialList.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return new LinkedHashMap<String, Object>() {
            {
                put("materialMapList", materialMapList);
                put("totalPrice", totalPrice);
                put("orderDetail", baseMapper.selectOne(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getCode, code)));
            }
        };
    }

    /**
     * 添加订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean saveOrder(OrderInfo orderInfo) {
        // 设置出库状态
        orderInfo.setStatus("1");
        // 添加订单编号
        orderInfo.setCode("ORDER-" + System.currentTimeMillis());
        orderInfo.setCreateTime(DateUtil.formatDateTime(new Date()));
        List<StorehouseInfo> infoList = JSONUtil.toList(orderInfo.getMaterial(), StorehouseInfo.class);
        BigDecimal totalPrice = infoList.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        orderInfo.setTotalPrice(totalPrice);
        // 设置出库编号
        infoList.forEach(e -> {
            e.setDeliveryOrderNumber(orderInfo.getCode());
            e.setCreateDate(DateUtil.formatDateTime(new Date()));
            e.setStatus("1");
        });
        storehouseInfoService.saveBatch(infoList);
        // 添加订单信息
        return this.save(orderInfo);
    }

    /**
     * 出库审核
     *
     * @param code 出库单号
     * @return 结果
     */
    @Override
    public boolean storeOutAudit(String code) {
        // 根据单号获取物品信息
        List<StorehouseInfo> infoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().eq(StorehouseInfo::getDeliveryOrderNumber, code));

        // 获取物料库存
        List<String> materialNameList = infoList.stream().map(StorehouseInfo::getMaterialName).distinct().collect(Collectors.toList());
        List<StorehouseInfo> storehouseInfoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().in(StorehouseInfo::getMaterialName, materialNameList).eq(StorehouseInfo::getTransactionType, 0));
        // 库存信息转MAP
        Map<String, StorehouseInfo> storehouseInfoMap = storehouseInfoList.stream().collect(Collectors.toMap(StorehouseInfo::getMaterialName, e -> e));
        List<StorehouseInfo> inStockList = new ArrayList<>();
        infoList.forEach(material -> {
            // 库房类型
            material.setTransactionType(2);
            material.setStatus("2");
            StorehouseInfo stockItem = storehouseInfoMap.get(material.getMaterialName());
            if (stockItem != null) {
                stockItem.setQuantity(stockItem.getQuantity().subtract(material.getQuantity()));
                material.setStatus(null);
                inStockList.add(stockItem);
            }
        });
        storehouseInfoService.updateBatchById(inStockList);
        storehouseInfoService.updateBatchById(infoList);
        return this.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getStatus, 2).eq(OrderInfo::getCode, code));
    }

    /**
     * 查询订单详情
     *
     * @param code 订单编号
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> orderDetail(String code) {
        return baseMapper.orderDetail(code);
    }

    /**
     * 月度统计【收益，支出】
     *
     * @param year  年度
     * @param month 季度
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStatisticsByMonth(String year, String month) {
        // 收入 【订单结余】
        List<OutStockRecord> outStockRecordList = outStockRecordMapper.selectOrderInfoByDate(year, month);
        // 支出【采收入库】
        List<StorageRecord> storageRecordList = storageRecordMapper.selectStorageRecordByDate(year, month);
        return new LinkedHashMap<String, Object>() {
            {
                put("orderTotal", outStockRecordList.size());
                put("orderTotalPrice", outStockRecordList.stream().map(OutStockRecord::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
                put("inTotal", storageRecordList.size());
                put("inTotalPrice", storageRecordList.stream().map(StorageRecord::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
        };
    }

    /**
     * 获取订单产品列表比率
     *
     * @param year  年度
     * @param month 季度
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectMaterialTypeRate(String year, String month) {
        // 获取订单信息
        List<OutStockRecord> orderInfoList = outStockRecordMapper.selectOrderInfoByDate(year, month);
        // 获取订单详情
        List<String> orderNumberList = orderInfoList.stream().map(OutStockRecord::getCode).collect(Collectors.toList());
        List<StorehouseInfo> storehouseInfoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(orderNumberList)) {
            storehouseInfoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().in(StorehouseInfo::getDeliveryOrderNumber, orderNumberList));
        }
        Map<String, List<StorehouseInfo>> materialSalesMap = storehouseInfoList.stream().collect(Collectors.groupingBy(StorehouseInfo::getMaterialName));
        Map<String, Object> materialSalesMapTop = new HashMap<>();
        materialSalesMap.forEach((key, value) -> {
            materialSalesMapTop.put(key, value.stream().map(StorehouseInfo::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add));
        });

        Map<Integer, List<StorehouseInfo>> storeTypeMap = storehouseInfoList.stream().collect(Collectors.groupingBy(StorehouseInfo::getMaterialType));
        // 总订单数量
        BigDecimal allOrderNumber = storehouseInfoList.stream().map(StorehouseInfo::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 总价格
        BigDecimal allPrice = storehouseInfoList.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        // 产品类型
        List<ProductTypeInfo> productTypeInfoList = productTypeInfoService.list();
        Map<Integer, String> productTypeMap = productTypeInfoList.stream().collect(Collectors.toMap(ProductTypeInfo::getId, ProductTypeInfo::getName));
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("allOrderNumber", allOrderNumber);
                put("allPrice", allPrice);
                put("materialSalesMapTop", materialSalesMapTop);
            }
        };
        storeTypeMap.forEach((key, value) -> {
            List<StorehouseInfo> item = storeTypeMap.get(key);
            if (CollectionUtil.isNotEmpty(item)) {
                LinkedHashMap<String, Object> items = new LinkedHashMap<String, Object>() {
                    {
                        put("orderNumber", item.stream().map(StorehouseInfo::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add));
                        put("price", item.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
                    }
                };
                result.put(productTypeMap.get(key), items);
            } else {
                LinkedHashMap<String, Object> items = new LinkedHashMap<String, Object>() {
                    {
                        put("orderNumber", BigDecimal.ZERO);
                        put("price", BigDecimal.ZERO);
                    }
                };
                result.put(productTypeMap.get(key), items);
            }
        });
        return result;
    }

    /**
     * 物料类别获取七天内统计信息
     *
     * @param materialType 物料类别
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectLastSevenDaysCount(Integer materialType) {
        return baseMapper.selectLastSevenDaysOrderCount(materialType);
    }
}
