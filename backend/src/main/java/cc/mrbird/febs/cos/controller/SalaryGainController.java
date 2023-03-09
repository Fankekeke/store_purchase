package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.SalaryGain;
import cc.mrbird.febs.cos.service.ISalaryGainService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/salary-gain")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SalaryGainController {

    private final ISalaryGainService salaryGainService;

    /**
     * 分页查询员工薪资涨幅情况
     *
     * @param page       分页对象
     * @param salaryGain 员工薪资涨幅信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<SalaryGain> page, SalaryGain salaryGain) {
        return R.ok(salaryGainService.selectSalaryPage(page, salaryGain));
    }

    /**
     * 根据员工编号获取当前员工薪资情况
     *
     * @param staffCode 员工编号
     * @return 结果
     */
    @GetMapping("/gain/{staffCode}")
    public R getGainByStaffCode(@PathVariable("staffCode") String staffCode) {
        SalaryGain salaryGain = salaryGainService.getOne(Wrappers.<SalaryGain>lambdaQuery().eq(SalaryGain::getStaffCode, staffCode).eq(SalaryGain::getCurrentFlag, 1));
        return R.ok(salaryGain != null ? salaryGain.getSalary() : BigDecimal.ZERO);
    }

    /**
     * 根据员工编号查询员工薪资涨幅
     *
     * @param code 员工编号
     * @return 结果
     */
    @GetMapping("/{code}")
    public R detail(@PathVariable("code") String code) {
        return R.ok(salaryGainService.salaryDetail(code));
    }

    /**
     * 添加员工薪资涨幅
     *
     * @param salaryGain 员工薪资涨幅
     * @return 结果
     */
    @PostMapping
    public R add(SalaryGain salaryGain) {
        return R.ok(salaryGainService.saveSalaryGain(salaryGain));
    }

    /**
     * 修改员工薪资涨幅
     *
     * @param salaryGain 员工薪资涨幅
     * @return 结果
     */
    @PutMapping
    public R edit(SalaryGain salaryGain) {
        return R.ok(salaryGainService.updateById(salaryGain));
    }

    /**
     * 删除员工薪资涨幅信息
     *
     * @param ids 员工薪资涨幅IDS
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(salaryGainService.removeByIds(ids));
    }
}
