package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.SalaryRecordsMapper;
import cc.mrbird.febs.cos.dao.StorageRecordMapper;
import cc.mrbird.febs.cos.entity.OrderInfo;
import cc.mrbird.febs.cos.dao.OrderInfoMapper;
import cc.mrbird.febs.cos.entity.SalaryRecords;
import cc.mrbird.febs.cos.entity.StorageRecord;
import cc.mrbird.febs.cos.entity.StorehouseInfo;
import cc.mrbird.febs.cos.service.IOrderInfoService;
import cc.mrbird.febs.cos.service.IStorehouseInfoService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
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

    private final SalaryRecordsMapper salaryRecordsMapper;

    private final StorageRecordMapper storageRecordMapper;

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
        // 添加订单编号
        orderInfo.setCode("ORDER-" + System.currentTimeMillis());
        orderInfo.setCreateTime(DateUtil.formatDateTime(new Date()));
        List<StorehouseInfo> infoList = JSONUtil.toList(orderInfo.getMaterial(), StorehouseInfo.class);
        BigDecimal totalPrice = infoList.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        orderInfo.setTotalPrice(totalPrice);
        // 设置出库编号
        infoList.forEach(e -> e.setDeliveryOrderNumber(orderInfo.getCode()));
        storehouseInfoService.saveBatch(infoList);
        // 添加订单信息
        return this.save(orderInfo);
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
        List<OrderInfo> orderInfoList = baseMapper.selectOrderInfoByDate(year, month);
        // 支出【采收入库+员工薪资】
        List<StorageRecord> storageRecordList = storageRecordMapper.selectStorageRecordByDate(year, month);
        List<SalaryRecords> salaryRecordsList = salaryRecordsMapper.selectList(Wrappers.<SalaryRecords>lambdaQuery().eq(SalaryRecords::getYear, year).eq(StrUtil.isNotEmpty(month), SalaryRecords::getMonth, month));
        return new LinkedHashMap<String, Object>() {
            {
                put("orderTotal", orderInfoList.size());
                put("orderTotalPrice", orderInfoList.stream().map(OrderInfo::getTotalPrice).reduce(BigDecimal.ZERO,BigDecimal::add));
                put("inTotal", storageRecordList.size());
                put("inTotalPrice", storageRecordList.stream().map(StorageRecord::getTotalPrice).reduce(BigDecimal.ZERO,BigDecimal::add));
                put("salaryTotal", salaryRecordsList.size());
                put("salaryTotalPrice", salaryRecordsList.stream().map(SalaryRecords::getPayroll).reduce(BigDecimal.ZERO,BigDecimal::add));
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
        List<OrderInfo> orderInfoList = baseMapper.selectOrderInfoByDate(year, month);
        // 获取订单详情
        List<String> orderNumberList = orderInfoList.stream().map(OrderInfo::getCode).collect(Collectors.toList());
        List<StorehouseInfo> storehouseInfoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().in(StorehouseInfo::getDeliveryOrderNumber, orderNumberList));
        Map<String, List<StorehouseInfo>> materialSalesMap = storehouseInfoList.stream().collect(Collectors.groupingBy(StorehouseInfo::getMaterialName));
        Map<String, Object> materialSalesMapTop = new HashMap<>();
        materialSalesMap.forEach((key, value) -> {
            materialSalesMapTop.put(key, value.stream().map(StorehouseInfo::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add));
        });

        Map<Integer, List<StorehouseInfo>> storeTypeMap = storehouseInfoList.stream().collect(Collectors.groupingBy(StorehouseInfo::getMaterialType));
        // 总订单数量
        BigDecimal allOrderNumber = storehouseInfoList.stream().map(StorehouseInfo::getQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
        // 总价格
        BigDecimal allPrice = storehouseInfoList.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

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
                result.put(key + "orderNumber", item.stream().map(StorehouseInfo::getQuantity).reduce(BigDecimal.ZERO,BigDecimal::add));
                result.put(key + "price", item.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
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
