package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.ReplenishmentInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IReplenishmentInfoService extends IService<ReplenishmentInfo> {

    /**
     * 分页查询盘库信息
     *
     * @param page              分页对象
     * @param replenishmentInfo 盘库信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectReplenishmentPage(Page<ReplenishmentInfo> page, ReplenishmentInfo replenishmentInfo);

    /**
     * 添加盘库信息
     *
     * @param replenishmentInfo 盘库信息
     * @return 结果
     */
    boolean saveReplenishment(ReplenishmentInfo replenishmentInfo);

    /**
     * 查询补货信息库存
     *
     * @param id 补货信息ID
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectReplenishmentInventory(Integer id) throws Exception;

}
