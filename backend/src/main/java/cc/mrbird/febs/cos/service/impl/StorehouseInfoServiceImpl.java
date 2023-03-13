package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.ReplenishmentInfo;
import cc.mrbird.febs.cos.entity.StorehouseInfo;
import cc.mrbird.febs.cos.dao.StorehouseInfoMapper;
import cc.mrbird.febs.cos.service.IReplenishmentInfoService;
import cc.mrbird.febs.cos.service.IStorehouseInfoService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorehouseInfoServiceImpl extends ServiceImpl<StorehouseInfoMapper, StorehouseInfo> implements IStorehouseInfoService {
    
    private final IReplenishmentInfoService replenishmentInfoService;
    
    /**
     * 分页查询库房信息
     *
     * @param page           分页对象
     * @param storehouseInfo 库房信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectStorehousePage(Page<StorehouseInfo> page, StorehouseInfo storehouseInfo) {
        return baseMapper.selectStorehousePage(page, storehouseInfo);
    }

    /**
     * 根据物料名称查询出入库记录
     *
     * @param name 物料名称
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStorehouseDetail(String name) {
        return new LinkedHashMap<String, Object>() {
            {
                put("in", baseMapper.selectStorehouseDetail(name, 1));
                put("out", baseMapper.selectStorehouseDetail(name, 2));
            }
        };
    }

    /**
     * 任务盘库
     *
     * @return 结果
     */
    @Override
    public void diskLibrary() {
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<StorehouseInfo> storehouseInfoList = this.list(Wrappers.<StorehouseInfo>lambdaQuery().eq(StorehouseInfo::getTransactionType, 0).lt(StorehouseInfo::getQuantity, 5));
        storehouseInfoList.forEach(item -> {
            LinkedHashMap<String, Object> resultItem = new LinkedHashMap<String, Object>() {
                {
                    put("materialName", item.getMaterialName());
                    put("materialType", item.getMaterialType());
                    put("model", item.getModel());
                    put("quantity", item.getQuantity());
                    put("measurementUnit", item.getMeasurementUnit());
                    put("unitPrice", item.getUnitPrice());
                }
            };
            if (item.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                sb.append("【").append(item.getMaterialName()).append("】库存余量不足，请及时补货");
            } else {
                sb.append("【").append(item.getMaterialName()).append("】库存余量不足，当前库存为 ").append(item.getQuantity()).append(item.getMeasurementUnit());
            }
            result.add(resultItem);
        });
        // 设置盘库记录
        ReplenishmentInfo replenishment = new ReplenishmentInfo();
        replenishment.setStatus(0);
        replenishment.setTaskDate(DateUtil.formatDate(new Date()));
        replenishment.setContent(StrUtil.isEmpty(sb.toString()) ? "库存充足" : sb.toString());
        replenishment.setReplenishment(JSONUtil.toJsonStr(result));
        replenishmentInfoService.save(replenishment);
    }

    /**
     * 远程调用物料信息
     *
     * @param materialName 物料名称
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectMaterialFuzzy(String materialName) {
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        List<StorehouseInfo> storehouseList = this.list(Wrappers.<StorehouseInfo>lambdaQuery().eq(StorehouseInfo::getTransactionType, 0).like(StorehouseInfo::getMaterialName, materialName).last("limit 8"));
        storehouseList.forEach(item -> {
            result.add(new LinkedHashMap<String, Object>() {
                {
                    put("materialName", item.getMaterialName());
                    put("materialType", item.getMaterialType());
                    put("model", item.getModel());
                    put("measurementUnit", item.getMeasurementUnit());
                    put("unitPrice", item.getUnitPrice());
                }
            });
        });
        return result;
    }
}
