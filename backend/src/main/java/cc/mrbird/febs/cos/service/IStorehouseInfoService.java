package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.StorehouseInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IStorehouseInfoService extends IService<StorehouseInfo> {

    /**
     * 分页查询库房信息
     *
     * @param page           分页对象
     * @param storehouseInfo 库房信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectStorehousePage(Page<StorehouseInfo> page, StorehouseInfo storehouseInfo);

    /**
     * 根据物料名称查询出入库记录
     *
     * @param name 物料名称
     * @return 结果
     */
    LinkedHashMap<String, Object> selectStorehouseDetail(String name);

    /**
     * 任务盘库
     *
     * @return 结果
     */
    void diskLibrary();

    /**
     * 远程调用物料信息
     *
     * @param materialName 物料名称
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectMaterialFuzzy(String materialName);

}
