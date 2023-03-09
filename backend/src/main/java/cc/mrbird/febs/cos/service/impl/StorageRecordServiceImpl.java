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
        // 设置入库单号
        storageRecord.setCode("IN-"+System.currentTimeMillis());
        List<StorehouseInfo> infoList = JSONUtil.toList(storageRecord.getMaterial(), StorehouseInfo.class);
        // 获取物料库存
        List<String> materialNameList = infoList.stream().map(StorehouseInfo::getMaterialName).distinct().collect(Collectors.toList());
        List<StorehouseInfo> storehouseInfoList = storehouseInfoService.list(Wrappers.<StorehouseInfo>lambdaQuery().in(StorehouseInfo::getMaterialName, materialNameList).eq(StorehouseInfo::getTransactionType, 0));
        // 库存信息转MAP
        Map<String, StorehouseInfo> storehouseInfoMap = storehouseInfoList.stream().collect(Collectors.toMap(StorehouseInfo::getMaterialName, e -> e));
        List<StorehouseInfo> inStockList = new ArrayList<>();
        List<StorehouseInfo> putStockList = new ArrayList<>();
        storageRecord.setCreateDate(DateUtil.formatDateTime(new Date()));
        // 总价格
        BigDecimal totalPrice = infoList.stream().map(p -> p.getQuantity().multiply(p.getUnitPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        storageRecord.setTotalPrice(totalPrice);
        infoList.forEach(material -> {
            // 入库单号
            material.setInboundOrderNumber(storageRecord.getCode());
            StorehouseInfo stockItem = storehouseInfoMap.get(material.getMaterialName());
            // 库房类型
            material.setTransactionType(1);
            material.setCreateDate(DateUtil.formatDateTime(new Date()));
            if (stockItem != null) {
                stockItem.setQuantity(stockItem.getQuantity().add(material.getQuantity()));
                stockItem.setCreateDate(DateUtil.formatDateTime(new Date()));
                inStockList.add(stockItem);
            } else {
                stockItem = BeanUtil.copyProperties(material, StorehouseInfo.class);
                stockItem.setInboundOrderNumber(null);
                stockItem.setTransactionType(0);
                putStockList.add(stockItem);
            }
        });
        if (CollectionUtil.isNotEmpty(inStockList)) {
            storehouseInfoService.updateBatchById(inStockList);
        }
        if (CollectionUtil.isNotEmpty(putStockList)) {
            infoList.addAll(putStockList);
        }
        storehouseInfoService.saveBatch(infoList);
        return this.save(storageRecord);
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
