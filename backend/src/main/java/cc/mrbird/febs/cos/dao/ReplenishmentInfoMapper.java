package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.ReplenishmentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ReplenishmentInfoMapper extends BaseMapper<ReplenishmentInfo> {

    /**
     * 分页查询盘库信息
     *
     * @param page              分页对象
     * @param replenishmentInfo 盘库信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectReplenishmentPage(Page<ReplenishmentInfo> page, @Param("replenishmentInfo") ReplenishmentInfo replenishmentInfo);
}
