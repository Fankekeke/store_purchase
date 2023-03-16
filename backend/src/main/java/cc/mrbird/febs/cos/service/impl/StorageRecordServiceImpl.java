package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.entity.StorageRecord;
import cc.mrbird.febs.cos.dao.StorageRecordMapper;
import cc.mrbird.febs.cos.entity.StorehouseInfo;
import cc.mrbird.febs.cos.service.IStorageRecordService;
import cc.mrbird.febs.cos.service.IStorehouseInfoService;
import cn.hutool.core.bean.BeanUtil;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorageRecordServiceImpl extends ServiceImpl<StorageRecordMapper, StorageRecord> implements IStorageRecordService {

    private final IStorehouseInfoService storehouseInfoService;

    /**
     * 分页查询入库记录
     *
     * @param page          分页对象
     * @param storageRecord 入库记录
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectStorageRecordPage(Page<StorageRecord> page, StorageRecord storageRecord) {
        return baseMapper.selectStorageRecordPage(page, storageRecord);
    }

    /**
     * 入库记录导出
     *
     * @param code 入库单号
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public LinkedHashMap<String, Object> export(String code) throws Exception {
        if (StrUtil.isEmpty(code)) {
            throw new FebsException("入库单号不能为空");
        }
        List<LinkedHashMap<String, Object>> materialMapList = baseMapper.storageRecordDetail(code);
        if (CollectionUtil.isEmpty(materialMapList)) {
            throw new FebsException("物料信息为空");
        }
        List<StorehouseInfo> materialList = JSONUtil.toList(JSONUtil.toJsonStr(materialMapList), StorehouseInfo.class);
        BigDecimal totalPrice = materialList.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return new LinkedHashMap<String, Object>() {
            {
                put("materialMapList", materialMapList);
                put("totalPrice", totalPrice);
                put("inDetail", baseMapper.selectOne(Wrappers.<StorageRecord>lambdaQuery().eq(StorageRecord::getCode, code)));
            }
        };
    }

    /**
     * 查询退货对比信息
     *
     * @param code 入库单号
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectReturnedPurchase(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        List<StorehouseInfo> goodsList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().eq(StorehouseInfo::getInboundOrderNumber, code));
        // 获取物品库存信息
        List<String> materialNameList = goodsList.stream().map(StorehouseInfo::getMaterialName).distinct().collect(Collectors.toList());
        List<StorehouseInfo> storehouseInfoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().in(StorehouseInfo::getMaterialName, materialNameList).eq(StorehouseInfo::getTransactionType, 0));
        // 库存信息转MAP
        Map<String, StorehouseInfo> storehouseInfoMap = storehouseInfoList.stream().collect(Collectors.toMap(StorehouseInfo::getMaterialName, e -> e));

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        goodsList.forEach(e -> {
            StorehouseInfo storeItem = storehouseInfoMap.get(e.getMaterialName());
            if (storeItem != null) {
                e.setStoreMax(storeItem.getQuantity());
            } else {
                e.setStoreMax(BigDecimal.ZERO);
            }
        });
        result.put("gooods", goodsList);
        result.put("status", goodsList.stream().noneMatch(e -> e.getStoreMax().compareTo(e.getQuantity()) < 0));
        return result;
    }

    /**
     * 退货操作
     *
     * @param code 入库单号
     * @return 结果
     */
    @Override
    public boolean returnedPurchase(String code) {
        if (StrUtil.isEmpty(code)) {
            return false;
        }
        StorageRecord storageRecord = this.getOne(Wrappers.<StorageRecord>lambdaQuery().eq(StorageRecord::getCode, code));
        // 入库物品
        List<StorehouseInfo> goodsList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().eq(StorehouseInfo::getInboundOrderNumber, code));

        List<String> materialNameList = goodsList.stream().map(StorehouseInfo::getMaterialName).distinct().collect(Collectors.toList());
        List<StorehouseInfo> storehouseInfoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().in(StorehouseInfo::getMaterialName, materialNameList).eq(StorehouseInfo::getTransactionType, 0));
        // 库存信息转MAP
        Map<String, StorehouseInfo> storehouseInfoMap = storehouseInfoList.stream().collect(Collectors.toMap(StorehouseInfo::getMaterialName, e -> e));

        // 需要更新的库存信息
        List<StorehouseInfo> inStockList = new ArrayList<>();
        // 更新库房库存
        goodsList.forEach(material -> {
            // 库存状态更改
            material.setStatus("3");
            // 此物品的库存信息
            StorehouseInfo stockItem = storehouseInfoMap.get(material.getMaterialName());

            if (stockItem != null) {
                stockItem.setQuantity(stockItem.getQuantity().subtract(material.getQuantity()));
                stockItem.setCreateDate(DateUtil.formatDateTime(new Date()));
                inStockList.add(stockItem);
            }
        });
        storehouseInfoService.updateBatchById(goodsList);
        storehouseInfoService.updateBatchById(inStockList);
        storageRecord.setStatus("3");
        return this.updateById(storageRecord);
    }

    /**
     * 入库记录详情
     *
     * @param code 入库单号
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> storageRecordDetail(String code) {
        return baseMapper.storageRecordDetail(code);
    }

    /**
     * 添加入库记录
     *
     * @param storageRecord 入库记录
     * @return 结果
     */
    @Override
    public boolean saveStorageRecord(StorageRecord storageRecord) {
        // 设置入库状态
        storageRecord.setStatus("1");
        // 设置入库单号
        storageRecord.setCode("IN-"+System.currentTimeMillis());
        List<StorehouseInfo> infoList = JSONUtil.toList(storageRecord.getMaterial(), StorehouseInfo.class);

        storageRecord.setCreateDate(DateUtil.formatDateTime(new Date()));
        // 总价格
        BigDecimal totalPrice = infoList.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        storageRecord.setTotalPrice(totalPrice);
        infoList.forEach(material -> {
            // 入库单号
            material.setInboundOrderNumber(storageRecord.getCode());
            // 库房类型
            material.setTransactionType(1);
            material.setStatus("1");
            material.setCreateDate(DateUtil.formatDateTime(new Date()));
        });
        storehouseInfoService.saveBatch(infoList);
        return this.save(storageRecord);
    }

    /**
     * 入库审核
     *
     * @param code 入库单号
     * @return 结果
     */
    @Override
    public boolean storePutAudit(String code) {
        // 根据入库单号获取入库物品信息
        List<StorehouseInfo> infoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaUpdate().eq(StorehouseInfo::getInboundOrderNumber, code));

        List<String> materialNameList = infoList.stream().map(StorehouseInfo::getMaterialName).distinct().collect(Collectors.toList());
        List<StorehouseInfo> storehouseInfoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().in(StorehouseInfo::getMaterialName, materialNameList).eq(StorehouseInfo::getTransactionType, 0));
        // 库存信息转MAP
        Map<String, StorehouseInfo> storehouseInfoMap = storehouseInfoList.stream().collect(Collectors.toMap(StorehouseInfo::getMaterialName, e -> e));

        // 需要更新的库存信息
        List<StorehouseInfo> inStockList = new ArrayList<>();
        List<StorehouseInfo> putStockList = new ArrayList<>();
        // 更新库房库存
        infoList.forEach(material -> {
            // 库存状态更改
            material.setStatus("2");
            // 此物品的库存信息
            StorehouseInfo stockItem = storehouseInfoMap.get(material.getMaterialName());

            if (stockItem != null) {
                stockItem.setQuantity(stockItem.getQuantity().add(material.getQuantity()));
                stockItem.setCreateDate(DateUtil.formatDateTime(new Date()));
                inStockList.add(stockItem);
            } else {
                stockItem = BeanUtil.copyProperties(material, StorehouseInfo.class);
                stockItem.setInboundOrderNumber(null);
                stockItem.setTransactionType(0);
                stockItem.setStatus(null);
                putStockList.add(stockItem);
            }
        });
        if (CollectionUtil.isNotEmpty(inStockList)) {
            storehouseInfoService.updateBatchById(inStockList);
        }
        if (CollectionUtil.isNotEmpty(putStockList)) {
            storehouseInfoService.saveBatch(putStockList);
        }
        storehouseInfoService.updateBatchById(infoList);

        // 修改入库单号状态
        return this.update(Wrappers.<StorageRecord>lambdaUpdate().set(StorageRecord::getStatus, 2).eq(StorageRecord::getCode, code));
    }

    /**
     * 分页查询入库统计
     *
     * @param materialType 物料类型
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectLastSevenDaysInCount(Integer materialType) {
        return baseMapper.selectLastSevenDaysInCount(materialType);
    }
}
