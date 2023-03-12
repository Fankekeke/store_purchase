package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.ProductTypeInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ProductTypeInfoMapper extends BaseMapper<ProductTypeInfo> {

    /**
     * 分页查询产品类别信息
     *
     * @param page         分页对象
     * @param productTypeInfo 产品类别信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectProductTypePage(Page<ProductTypeInfo> page, @Param("productTypeInfo") ProductTypeInfo productTypeInfo);
}
