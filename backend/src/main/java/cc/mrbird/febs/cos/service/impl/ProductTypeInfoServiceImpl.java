package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.ProductTypeInfo;
import cc.mrbird.febs.cos.dao.ProductTypeInfoMapper;
import cc.mrbird.febs.cos.service.IProductTypeInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class ProductTypeInfoServiceImpl extends ServiceImpl<ProductTypeInfoMapper, ProductTypeInfo> implements IProductTypeInfoService {

    /**
     * 分页查询产品类别信息
     *
     * @param page         分页对象
     * @param productTypeInfo 产品类别信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectProductTypePage(Page<ProductTypeInfo> page, ProductTypeInfo productTypeInfo) {
        return baseMapper.selectProductTypePage(page, productTypeInfo);
    }
}
