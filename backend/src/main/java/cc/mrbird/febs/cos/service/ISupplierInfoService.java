package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.SupplierInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ISupplierInfoService extends IService<SupplierInfo> {

    /**
     * 分页查询供应商信息
     *
     * @param page         分页对象
     * @param supplierInfo 供应商信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectSupplierPage(Page<SupplierInfo> page, SupplierInfo supplierInfo);
}
