package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.SalaryGain;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.service.ISalaryGainService;
import cc.mrbird.febs.cos.service.IStaffInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/staff-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffInfoController {

    private final IStaffInfoService staffInfoService;

    private final ISalaryGainService salaryGainService;

    /**
     * 分页查询员工信息
     *
     * @param page      分页对象
     * @param staffInfo 员工信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<StaffInfo> page, StaffInfo staffInfo) {
        return R.ok(staffInfoService.selectStaffPage(page, staffInfo));
    }

    /**
     * 员工信息详情
     *
     * @param code 员工编号
     * @return 结果
     */
    @GetMapping("{code}")
    public R detail(@PathVariable("code") String code) {
        return R.ok(staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getStaffCode, code)));
    }

    /**
     * 添加员工信息
     *
     * @param staffInfo 员工信息
     * @return 结果
     */
    @PostMapping
    public R add(StaffInfo staffInfo) {
        // 设置员工编号
        staffInfo.setStaffCode("STAFF-" + System.currentTimeMillis());
        staffInfo.setOnBoardTime(DateUtil.formatDate(new Date()));
        staffInfo.setStaffStatus(1);

        SalaryGain salaryGain = new SalaryGain();
        salaryGain.setStaffCode(staffInfo.getStaffCode());
        salaryGain.setType(0);
        salaryGain.setSalary(staffInfo.getSalary());
        salaryGain.setCurrentFlag(1);
        salaryGain.setCreateDate(DateUtil.formatDate(new Date()));
        salaryGainService.save(salaryGain);
        return R.ok(staffInfoService.save(staffInfo));
    }

    /**
     * 修改员工信息
     *
     * @param staffInfo 员工信息
     * @return 结果
     */
    @PutMapping
    public R edit(StaffInfo staffInfo) {
        return R.ok(staffInfoService.updateById(staffInfo));
    }

    /**
     * 删除员工信息
     *
     * @param ids 员工信息IDS
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(staffInfoService.removeByIds(ids));
    }

    /**
     * 员工离职
     *
     * @param staffCode 员工编号
     * @return 结果
     */
    @GetMapping("/resign/{staffCode}")
    public R resign(@PathVariable("staffCode") String staffCode) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getStaffCode, staffCode));
        staffInfo.setResignTime(DateUtil.formatDate(new Date()));
        staffInfo.setStaffStatus(2);
        return R.ok(staffInfoService.updateById(staffInfo));
    }

    /**
     * 所有员工信息
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(staffInfoService.list(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getStaffStatus, 1)));
    }
}
