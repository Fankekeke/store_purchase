package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.StorehouseInfo;
import cc.mrbird.febs.cos.service.IStorehouseInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/storehouse-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorehouseInfoController {

    private final IStorehouseInfoService storehouseInfoService;

    /**
     * 分页查询库房信息
     *
     * @param page           分页对象
     * @param storehouseInfo 库房信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<StorehouseInfo> page, StorehouseInfo storehouseInfo) {
        return R.ok(storehouseInfoService.selectStorehousePage(page, storehouseInfo));
    }

    /**
     * 根据物料名称查询出入库记录
     *
     * @param name 物料名称
     * @return 结果
     */
    @GetMapping("{name}")
    public R detail(@PathVariable("name") String name) {
        return R.ok(storehouseInfoService.selectStorehouseDetail(name));
    }

    /**
     * 删除库房信息
     *
     * @param ids 库房信息IDS
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(storehouseInfoService.removeByIds(ids));
    }

    /**
     * 远程调用物料信息
     *
     * @param materialName 物料名称
     * @return 结果
     */
    @GetMapping("/remote/{materialName}")
    public R selectMaterialFuzzy(@PathVariable("materialName") String materialName) {
        return R.ok(storehouseInfoService.selectMaterialFuzzy(materialName));
    }

    /**
     * 盘库
     */
    @GetMapping("/replenishment")
    public R replenishment() {
        storehouseInfoService.diskLibrary();
        return R.ok();
    }
}
