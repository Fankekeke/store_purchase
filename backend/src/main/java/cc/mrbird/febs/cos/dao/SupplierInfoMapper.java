package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.SupplierInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface SupplierInfoMapper extends BaseMapper<SupplierInfo> {

    /**
     * 分页查询供应商信息
     *
     * @param page         分页对象
     * @param supplierInfo 供应商信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectSupplierPage(Page<SupplierInfo> page, @Param("supplierInfo") SupplierInfo supplierInfo);
}
