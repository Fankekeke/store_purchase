package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 分页查询订单信息
     *
     * @param page      分页对象
     * @param orderInfo 订单信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectOrderPage(Page<OrderInfo> page, @Param("orderInfo") OrderInfo orderInfo);

    /**
     * 查询订单详情
     *
     * @param code 订单编号
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> orderDetail(@Param("code") String code);

    /**
     * 根据时间查询订单信息
     *
     * @param year  年度
     * @param month 季度
     * @return 结果
     */
    List<OrderInfo> selectOrderInfoByDate(@Param("year") String year, @Param("month") String month);

    /**
     * 七天内订单统计
     *
     * @param materialType 物料类型
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectLastSevenDaysOrderCount(@Param("materialType") Integer materialType);

}
