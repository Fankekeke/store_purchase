package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.OrderInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    /**
     * 分页查询订单信息
     *
     * @param page      分页对象
     * @param orderInfo 订单信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectOrderPage(Page<OrderInfo> page, OrderInfo orderInfo);

    /**
     * 订单详情导出
     *
     * @param code 订单编号
     * @return 结果
     * @throws Exception 异常
     */
    LinkedHashMap<String, Object> export(String code) throws Exception;

    /**
     * 添加订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    boolean saveOrder(OrderInfo orderInfo);

    /**
     * 查询订单详情
     *
     * @param code 订单编号
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> orderDetail(String code);

    /**
     * 月度统计【收益，支出，订单出入库类型】
     *
     * @param year  年度
     * @param month 季度
     * @return 结果
     */
    LinkedHashMap<String, Object> selectStatisticsByMonth(String year, String month);

    /**
     * 获取订单产品列表比率
     *
     * @param year  年度
     * @param month 季度
     * @return 结果
     */
    LinkedHashMap<String, Object> selectMaterialTypeRate(String year, String month);

    /**
     * 物料类别获取七天内统计信息
     *
     * @param materialType 物料类别
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectLastSevenDaysCount(Integer materialType);
}
