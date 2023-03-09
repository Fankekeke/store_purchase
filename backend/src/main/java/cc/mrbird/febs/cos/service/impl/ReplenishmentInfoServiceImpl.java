package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.StaffInfoMapper;
import cc.mrbird.febs.cos.dao.StorehouseInfoMapper;
import cc.mrbird.febs.cos.entity.ReplenishmentInfo;
import cc.mrbird.febs.cos.dao.ReplenishmentInfoMapper;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.entity.StorehouseInfo;
import cc.mrbird.febs.cos.service.IMailService;
import cc.mrbird.febs.cos.service.IReplenishmentInfoService;
import cc.mrbird.febs.cos.service.IStaffInfoService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReplenishmentInfoServiceImpl extends ServiceImpl<ReplenishmentInfoMapper, ReplenishmentInfo> implements IReplenishmentInfoService {

    private final StaffInfoMapper staffInfoMapper;

    private final StorehouseInfoMapper storehouseInfoMapper;

    private final TemplateEngine templateEngine;

    private final IMailService mailService;

    /**
     * 分页查询盘库信息
     *
     * @param page              分页对象
     * @param replenishmentInfo 盘库信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectReplenishmentPage(Page<ReplenishmentInfo> page, ReplenishmentInfo replenishmentInfo) {
        return baseMapper.selectReplenishmentPage(page, replenishmentInfo);
    }

    /**
     * 添加盘库信息
     *
     * @param replenishmentInfo 盘库信息
     * @return 结果
     */
    @Override
    public boolean saveReplenishment(ReplenishmentInfo replenishmentInfo) {
        replenishmentInfo.setTaskDate(DateUtil.formatDate(new Date()));
        // 获取经手人信息
        StaffInfo staff = staffInfoMapper.selectOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getStaffCode, replenishmentInfo.getStaffCode()));
        if (staff != null && StrUtil.isNotEmpty(staff.getEmail())) {
            // 发送邮件
            Context context = new Context();
            context.setVariable("today", DateUtil.formatDate(new Date()));
            context.setVariable("custom", staff.getStaffName() + "，您好。新的盘库记录已生成，请注意补货");
            String emailContent = templateEngine.process("registerEmail", context);
            mailService.sendHtmlMail(staff.getEmail(), DateUtil.formatDate(new Date()) + "盘库统计", emailContent);
        }
        return this.save(replenishmentInfo);
    }

    /**
     * 查询补货信息库存
     *
     * @param id 补货信息ID
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectReplenishmentInventory(Integer id) throws Exception {
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        // 获取补货信息
        ReplenishmentInfo replenishmentInfo = this.getById(id);
        if (StrUtil.isEmpty(replenishmentInfo.getReplenishment())) {
            throw new FebsException("无需要补货信息！");
        }
        List<StorehouseInfo> storehouseList = Convert.toList(StorehouseInfo.class, replenishmentInfo.getReplenishment());
        List<String> materialNameList = storehouseList.stream().map(StorehouseInfo::getMaterialName).distinct().collect(Collectors.toList());
        // 获取库存信息
        List<StorehouseInfo> infoList = storehouseInfoMapper.selectList(Wrappers.<StorehouseInfo>lambdaQuery().eq(StorehouseInfo::getTransactionType, 0).in(StorehouseInfo::getMaterialName, materialNameList));
        Map<String, BigDecimal> materialInventory = infoList.stream().collect(Collectors.toMap(StorehouseInfo::getMaterialName, StorehouseInfo::getQuantity));
        storehouseList.forEach(item -> {
            LinkedHashMap<String, Object> resultItem = new LinkedHashMap<String, Object>() {
                {
                    put("materialName", item.getMaterialName());
                    put("materialType", item.getMaterialType());
                    put("unitPrice", item.getUnitPrice());
                    put("replenishmentQuantity", item.getQuantity());
                    put("inventory", materialInventory.get(item.getMaterialName()) != null ? materialInventory.get(item.getMaterialName()) : 0);
                }
            };
            result.add(resultItem);
        });
        return result;
    }
}
