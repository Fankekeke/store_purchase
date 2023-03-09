package cc.mrbird.febs.cos.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 供应商管理
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupplierInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 可供采购类型（1.食品生鲜 2.家用电器 3.办公用品 4.日常杂货）
     */
    private Integer purchaseType;


}
