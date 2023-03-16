package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.OutStockRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface OutStockRecordMapper extends BaseMapper<OutStockRecord> {

    /**
     * 分页查询出库记录
     *
     * @param page           分页对象
     * @param outStockRecord 出库信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectOutStockRecordPage(Page<OutStockRecord> page, @Param("outStockRecord") OutStockRecord outStockRecord);

    /**
     * 查询出库记录详情
     *
     * @param code 出库单号
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> outStockDetail(@Param("code") String code);

    /**
     * 根据年份月度查询出库信息
     *
     * @param year  年份
     * @param month 月份
     * @return 结果
     */
    List<OutStockRecord> selectOrderInfoByDate(@Param("year") String year, @Param("month") String month);

    /**
     * 七天内出库统计
     *
     * @param materialType 物料类型
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectLastSevenDaysOutCount(@Param("materialType") Integer materialType);
}
