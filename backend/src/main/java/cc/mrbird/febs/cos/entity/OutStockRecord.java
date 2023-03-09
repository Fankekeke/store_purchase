package cc.mrbird.febs.cos.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 出库记录
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OutStockRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 出库编号
     */
    private String code;

    /**
     * 经手人
     */
    private String handlerCode;

    /**
     * 保管人
     */
    private String custodianCode;

    /**
     * 出库时间
     */
    private String createDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 物料信息
     */
    @TableField(exist = false)
    private String material;

    @TableField(exist = false)
    private String handlerName;

    @TableField(exist = false)
    private String custodianName;
}
