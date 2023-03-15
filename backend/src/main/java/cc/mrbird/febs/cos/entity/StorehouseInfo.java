package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 库房管理
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorehouseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 物料名称
     */
    private String materialName;

    /**
     * 物料类型（1.食品生鲜 2.家用电器 3.办公用品 4.日常杂货）
     */
    private Integer materialType;

    /**
     * 计量单位
     */
    private String measurementUnit;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 交易类型（0.库房存货 1.入库信息 2.出库信息）
     */
    private Integer transactionType;

    /**
     * 操作时间
     */
    private String createDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 型号
     */
    private String model;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 入库单号
     */
    private String inboundOrderNumber;

    /**
     * 出库单号
     */
    private String deliveryOrderNumber;

    /**
     * 入库出库状态（1.未更新 2.已更新）
     */
    private String status;


}
