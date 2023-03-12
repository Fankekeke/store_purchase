package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ProductTypeInfo;
import cc.mrbird.febs.cos.service.IProductTypeInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/product-type-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductTypeInfoController {

    private final IProductTypeInfoService productTypeInfoService;

    /**
     * 分页查询产品类别信息
     *
     * @param page            分页对象
     * @param productTypeInfo 产品类别信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<ProductTypeInfo> page, ProductTypeInfo productTypeInfo) {
        return R.ok(productTypeInfoService.selectProductTypePage(page, productTypeInfo));
    }

    /**
     * 查询所有产品类别
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(productTypeInfoService.list());
    }

    /**
     * 添加产品类别信息
     *
     * @param productTypeInfo 产品类别信息
     * @return 结果
     */
    @PostMapping
    public R add(ProductTypeInfo productTypeInfo) {
        productTypeInfo.setCreateDate(DateUtil.formatDate(new Date()));
        productTypeInfo.setCode("PT-" + System.currentTimeMillis());
        return R.ok(productTypeInfoService.save(productTypeInfo));
    }

    /**
     * 修改产品类别信息
     *
     * @param productTypeInfo 产品类别信息
     * @return 结果
     */
    @PutMapping
    public R edit(ProductTypeInfo productTypeInfo) {
        return R.ok(productTypeInfoService.updateById(productTypeInfo));
    }

    /**
     * 删除产品类别信息
     *
     * @param ids 产品类别信息IDS
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(productTypeInfoService.removeByIds(ids));
    }
}
