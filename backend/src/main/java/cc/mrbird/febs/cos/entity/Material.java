package cc.mrbird.febs.cos.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Material implements Serializable {

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
     * 型号
     */
    private String model;

    /**
     * 单价
     */
    private BigDecimal unitPrice;
}
