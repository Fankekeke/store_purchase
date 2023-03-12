package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.ProductTypeInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IProductTypeInfoService extends IService<ProductTypeInfo> {

    /**
     * 分页查询产品类别信息
     *
     * @param page         分页对象
     * @param productTypeInfo 产品类别信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectProductTypePage(Page<ProductTypeInfo> page, ProductTypeInfo productTypeInfo);
}
