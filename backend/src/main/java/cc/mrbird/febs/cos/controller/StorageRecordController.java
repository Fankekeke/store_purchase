package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.StorageRecord;
import cc.mrbird.febs.cos.service.IStorageRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/storage-record")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorageRecordController {

    private final IStorageRecordService storageRecordService;

    /**
     * 分页查询入库记录
     *
     * @param page          分页对象
     * @param storageRecord 入库记录
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<StorageRecord> page, StorageRecord storageRecord) {
        return R.ok(storageRecordService.selectStorageRecordPage(page, storageRecord));
    }

    /**
     * 入库记录导出
     *
     * @param code 入库单号
     * @return 结果
     * @throws Exception 异常
     */
    @GetMapping("/export/{code}")
    public R export(@PathVariable("code") String code) throws Exception {
        return R.ok(storageRecordService.export(code));
    }

    /**
     * 入库记录详情
     *
     * @param code 入库单号
     * @return 结果
     */
    @GetMapping("/{code}")
    public R detail(@PathVariable("code") String code) {
        return R.ok(storageRecordService.storageRecordDetail(code));
    }

    /**
     * 添加入库记录
     *
     * @param storageRecord 入库记录
     * @return 结果
     */
    @PostMapping
    public R add(StorageRecord storageRecord) {
        return R.ok(storageRecordService.saveStorageRecord(storageRecord));
    }

    /**
     * 修改入库记录
     *
     * @param storageRecord 入库记录
     * @return 结果
     */
    @PutMapping
    public R edit(StorageRecord storageRecord) {
        return R.ok(storageRecordService.updateById(storageRecord));
    }

    /**
     * 删除入库记录
     *
     * @param ids 入库记录IDS
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(storageRecordService.removeByIds(ids));
    }

}
