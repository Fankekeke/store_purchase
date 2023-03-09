package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ReplenishmentInfo;
import cc.mrbird.febs.cos.service.IReplenishmentInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/replenishment-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReplenishmentInfoController {

    private final IReplenishmentInfoService replenishmentInfoService;

    /**
     * 分页查询盘库信息
     *
     * @param page              分页对象
     * @param replenishmentInfo 盘库信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<ReplenishmentInfo> page, ReplenishmentInfo replenishmentInfo) {
        return R.ok(replenishmentInfoService.selectReplenishmentPage(page, replenishmentInfo));
    }

    /**
     * 查询补货信息库存
     *
     * @param id 补货信息ID
     * @return 结果
     */
    @GetMapping("/inventory/{id}")
    public R selectReplenishmentInventory(@PathVariable("id") Integer id) throws Exception {
        return R.ok(replenishmentInfoService.selectReplenishmentInventory(id));
    }

    /**
     * 修改补货信息状态
     *
     * @param id 补货信息ID
     * @return 结果
     */
    @GetMapping("/edit/status/{id}")
    public R updateReplenishmentStatus(@PathVariable("id") Integer id) {
        return R.ok(replenishmentInfoService.update(Wrappers.<ReplenishmentInfo>lambdaUpdate().set(ReplenishmentInfo::getStatus, 1).eq(ReplenishmentInfo::getId, id)));
    }

}
