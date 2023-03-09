package cc.mrbird.febs.cos.entity;

import java.time.LocalDate;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 补货通知
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReplenishmentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 预警时间
     */
    private String taskDate;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 经手人
     */
    private String staffCode;

    /**
     * 补货状态（0.未补货 1.已补货）
     */
    private Integer status;

    /**
     * 补货信息
     */
    private String replenishment;

    /**
     * 员工姓名
     */
    @TableField(exist = false)
    private String staffName;

}
