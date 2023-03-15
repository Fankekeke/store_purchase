package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.OutStockRecord;
import cc.mrbird.febs.cos.service.IOutStockRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/out-stock-record")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OutStockRecordController {

    private final IOutStockRecordService outStockRecordService;

    /**
     * 分页查询出库记录
     *
     * @param page           分页对象
     * @param outStockRecord 出库信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<OutStockRecord> page, OutStockRecord outStockRecord) {
        return R.ok(outStockRecordService.selectOutStockRecordPage(page, outStockRecord));
    }

    /**
     * 出库信息导出
     *
     * @param code 出库单号
     * @return 结果
     */
    @GetMapping("/export/{code}")
    public R export(@PathVariable("code") String code) throws Exception {
        return R.ok(outStockRecordService.export(code));
    }

    /**
     * 查询出库记录详情
     *
     * @param code 出库单号
     * @return 结果1
     */
    @GetMapping("/{code}")
    public R detail(@PathVariable("code") String code) {
        return R.ok(outStockRecordService.outStockDetail(code));
    }

    /**
     * 添加物料出库信息
     *
     * @param outStockRecord 出库记录
     * @return 结果
     */
    @PostMapping
    public R add(OutStockRecord outStockRecord) {
        return R.ok(outStockRecordService.saveOutStock(outStockRecord));
    }

    /**
     * 出库审核
     *
     * @param code 出库单号
     * @return 结果
     */
    @GetMapping("/storeOutAudit/{code}")
    public R storeOutAudit(@PathVariable("code") String code) {
        return R.ok(outStockRecordService.storeOutAudit(code));
    }

    /**
     * 编辑物料出库信息
     *
     * @param outStockRecord 出库记录
     * @return 结果
     */
    @PutMapping
    public R edit(OutStockRecord outStockRecord) {
        return R.ok(outStockRecordService.updateById(outStockRecord));
    }

    /**
     * 删除出库信息
     *
     * @param ids 出库记录IDS
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(outStockRecordService.removeByIds(ids));
    }

}
