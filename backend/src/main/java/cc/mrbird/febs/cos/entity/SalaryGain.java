package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 员工薪资涨幅
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SalaryGain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 员工编号
     */
    private String staffCode;

    /**
     * 当前薪资
     */
    private BigDecimal salary;

    /**
     * 是否为当前薪资（0.否 1.是）
     */
    private Integer currentFlag;

    /**
     * 调整时间
     */
    private String createDate;

    /**
     * 调整类型（1.上涨 2.跌幅）
     */
    private Integer type;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private String staffName;

    @TableField(exist = false)
    private Integer staffType;
}
