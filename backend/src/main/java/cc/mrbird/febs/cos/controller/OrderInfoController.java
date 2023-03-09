package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.OrderInfo;
import cc.mrbird.febs.cos.service.IOrderInfoService;
import cc.mrbird.febs.cos.service.IOutStockRecordService;
import cc.mrbird.febs.cos.service.IStorageRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/order-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoController {

    private final IOrderInfoService orderInfoService;

    private final IOutStockRecordService outStockRecordService;

    private final IStorageRecordService storageRecordService;


    /**
     * 分页查询订单信息
     *
     * @param page      分页对象
     * @param orderInfo 订单信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<OrderInfo> page, OrderInfo orderInfo) {
        return R.ok(orderInfoService.selectOrderPage(page, orderInfo));
    }

    /**
     * 订单详情导出
     *
     * @param code 订单编号
     * @return 结果
     * @throws Exception 异常
     */
    @GetMapping("/export/{code}")
    public R export(@PathVariable("code") String code) throws Exception {
        return R.ok(orderInfoService.export(code));
    }

    /**
     * 查询订单详情
     *
     * @param code 订单编号
     * @return 结果
     */
    @GetMapping("/{code}")
    public R detail(@PathVariable("code") String code) {
        return R.ok(orderInfoService.orderDetail(code));
    }

    /**
     * 添加订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @PostMapping
    public R add(OrderInfo orderInfo) {
        return R.ok(orderInfoService.saveOrder(orderInfo));
    }

    /**
     * 修改订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @PutMapping
    public R edit(OrderInfo orderInfo) {
        return R.ok(orderInfoService.updateById(orderInfo));
    }

    /**
     * 删除订单信息
     *
     * @param ids 订单信息IDS
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(orderInfoService.removeByIds(ids));
    }

    /**
     * 月度统计【收益，支出】
     *
     * @param year  年度
     * @param month 季度
     * @return 结果
     */
    @GetMapping("/statistics")
    public R selectStatisticsByMonth(@RequestParam("year") String year, @RequestParam(value = "month", required = false) String month) {
        return R.ok(orderInfoService.selectStatisticsByMonth(year, month));
    }

    /**
     * 获取订单产品列表比率
     *
     * @param year  年度
     * @param month 季度
     * @return 结果
     */
    @GetMapping("/statistics/rate")
    public R selectStatisticsByYear(@RequestParam("year") String year, @RequestParam(value = "month", required = false) String month) {
        return R.ok(orderInfoService.selectMaterialTypeRate(year, month));
    }

    /**
     * 七天内统计
     *
     * @param materialType 物料类型
     * @return 结果
     */
    @GetMapping("/seven/count")
    public R selectLastSevenDaysCount(Integer materialType) {
        return R.ok(new LinkedHashMap<String, Object>() {
            {
                put("order", orderInfoService.selectLastSevenDaysCount(materialType));
                put("out", outStockRecordService.selectLastSevenDaysOutCount(materialType));
                put("in", storageRecordService.selectLastSevenDaysInCount(materialType));
            }
        });
    }
}
