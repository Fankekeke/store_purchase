package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.StorehouseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface StorehouseInfoMapper extends BaseMapper<StorehouseInfo> {

    /**
     * 分页查询库房信息
     *
     * @param page           分页对象
     * @param storehouseInfo 库房信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectStorehousePage(Page<StorehouseInfo> page, @Param("storehouseInfo") StorehouseInfo storehouseInfo);

    /**
     * 根据物料名称查询出入库记录
     *
     * @param name            物料名称
     * @param transactionType 交易类型（0.库房存货 1.入库信息 2.出库信息）
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectStorehouseDetail(@Param("name") String name, @Param("transactionType") Integer transactionType);

}
