package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.SalaryGain;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ISalaryGainService extends IService<SalaryGain> {

    /**
     * 分页查询员工薪资涨幅情况
     *
     * @param page       分页对象
     * @param salaryGain 员工薪资涨幅信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectSalaryPage(Page<SalaryGain> page, SalaryGain salaryGain);

    /**
     * 根据员工编号查询员工薪资涨幅
     *
     * @param code 员工编号
     * @return 结果
     */
    LinkedHashMap<String, Object> salaryDetail(String code);

    /**
     * 添加员工薪资涨幅
     *
     * @param salaryGain 员工薪资涨幅
     * @return 结果
     */
    boolean saveSalaryGain(SalaryGain salaryGain);
}
