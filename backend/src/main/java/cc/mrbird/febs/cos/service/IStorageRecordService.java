package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.StorageRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IStorageRecordService extends IService<StorageRecord> {

    /**
     * 分页查询入库记录
     *
     * @param page          分页对象
     * @param storageRecord 入库记录
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectStorageRecordPage(Page<StorageRecord> page, StorageRecord storageRecord);

    /**
     * 入库记录导出
     *
     * @param code 入库单号
     * @return 结果
     * @throws Exception 异常
     */
    LinkedHashMap<String, Object> export(String code) throws Exception;

    /**
     * 入库记录详情
     *
     * @param code 入库单号
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> storageRecordDetail(String code);

    /**
     * 添加入库记录
     *
     * @param storageRecord 入库记录
     * @return 结果
     */
    boolean saveStorageRecord(StorageRecord storageRecord);

    /**
     * 入库审核
     *
     * @param code 入库单号
     * @return 结果
     */
    boolean storePutAudit(String code);

    /**
     * 分页查询入库统计
     *
     * @param materialType 物料类型
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectLastSevenDaysInCount(Integer materialType);
}
