package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 员工信息
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StaffInfo implements Serializable {

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
     * 员工姓名
     */
    private String staffName;

    /**
     * 员工类型（1.售货员 2.理货员 3.收银员 4.分拣员 5.杂工）
     */
    private Integer staffType;

    /**
     * 性别（1.男 2.女）
     */
    private Integer staffSex;

    /**
     * 员工状态（1.已入职 2.已离职）
     */
    private Integer staffStatus;

    /**
     * 员工头像
     */
    private String avatar;

    /**
     * 入职时间
     */
    private String onBoardTime;

    /**
     * 离职时间
     */
    private String resignTime;

    /**
     * 出生日期
     */
    private String birthDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 邮箱地址
     */
    private String email;


    @TableField(exist = false)
    private BigDecimal salary;

}
