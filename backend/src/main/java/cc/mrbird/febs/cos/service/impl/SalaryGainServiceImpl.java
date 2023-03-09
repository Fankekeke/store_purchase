package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.SalaryGain;
import cc.mrbird.febs.cos.dao.SalaryGainMapper;
import cc.mrbird.febs.cos.service.ISalaryGainService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class SalaryGainServiceImpl extends ServiceImpl<SalaryGainMapper, SalaryGain> implements ISalaryGainService {

    /**
     * 分页查询员工薪资涨幅情况
     *
     * @param page       分页对象
     * @param salaryGain 员工薪资涨幅信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectSalaryPage(Page<SalaryGain> page, SalaryGain salaryGain) {
        return baseMapper.selectSalaryPage(page, salaryGain);
    }

    /**
     * 根据员工编号查询员工薪资涨幅
     *
     * @param code 员工编号
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> salaryDetail(String code) {
        return new LinkedHashMap<String, Object>() {
            {
                put("record", baseMapper.selectSalaryByStaffCode(code));
            }
        };
    }

    /**
     * 添加员工薪资涨幅
     *
     * @param salaryGain 员工薪资涨幅
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean saveSalaryGain(SalaryGain salaryGain) {
        // 获取上次调薪记录
        salaryGain.setType(1);
        SalaryGain lastSalaryGain = this.getOne(Wrappers.<SalaryGain>lambdaQuery().eq(SalaryGain::getStaffCode, salaryGain.getStaffCode()).eq(SalaryGain::getCurrentFlag, 1));
        if (lastSalaryGain != null) {
            salaryGain.setType(salaryGain.getSalary().compareTo(lastSalaryGain.getSalary()) > 0 ? 1 : 2);
        }
        // 设置为当前薪资
        salaryGain.setCurrentFlag(1);
        this.update(Wrappers.<SalaryGain>lambdaUpdate().set(SalaryGain::getCurrentFlag, 0).eq(SalaryGain::getStaffCode, salaryGain.getStaffCode()));
        salaryGain.setCreateDate(DateUtil.formatDateTime(new Date()));
        return this.save(salaryGain);
    }
}
