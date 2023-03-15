package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.OutStockRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IOutStockRecordService extends IService<OutStockRecord> {

    /**
     * 分页查询出库记录
     *
     * @param page           分页对象
     * @param outStockRecord 出库信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectOutStockRecordPage(Page<OutStockRecord> page, OutStockRecord outStockRecord);

    /**
     * 出库信息导出
     *
     * @param code 出库单号
     * @return 结果
     */
    LinkedHashMap<String, Object> export(String code) throws Exception;
    /**
     * 查询出库记录详情
     *
     * @param code 出库单号
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> outStockDetail(String code);

    /**
     * 添加物料出库信息
     *
     * @param outStockRecord 出库记录
     * @return 结果
     */
    boolean saveOutStock(OutStockRecord outStockRecord);

    /**
     * 出库审核
     *
     * @param code 出库单号
     * @return 结果
     */
    boolean storeOutAudit(String code);

    /**
     * 七天内出库统计
     *
     * @param materialType 物料类型
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectLastSevenDaysOutCount(Integer materialType);
}
