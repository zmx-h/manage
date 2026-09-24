package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 修改资产 请求体。
 *
 * <p>对应接口：4-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资产 ID */
    @Schema(description = "资产 ID")
    @NotNull(message = "资产 ID不能为空")
    private Long assetId;

    /** 资产编号，全局唯一 */
    @Schema(description = "资产编号，全局唯一")
    @NotBlank(message = "资产编号，全局唯一不能为空")
    private String assetNo;

    /** 资产名称，最长 100 字 */
    @Schema(description = "资产名称，最长 100 字")
    @NotBlank(message = "资产名称，最长 100 字不能为空")
    private String name;

    /** 资产分类 ID */
    @Schema(description = "资产分类 ID")
    private Long categoryId;

    /** 品牌 */
    @Schema(description = "品牌")
    private String brand;

    /** 型号 */
    @Schema(description = "型号")
    private String model;

    /** 购置价格 */
    @Schema(description = "购置价格")
    private BigDecimal price;

    /** 购置日期 */
    @Schema(description = "购置日期")
    private String purchaseDate;

    /** 供应商 */
    @Schema(description = "供应商")
    private String supplier;

    /** 1 采购，2 捐赠，3 调拨 */
    @Schema(description = "1 采购，2 捐赠，3 调拨")
    private Integer sourceType;

    /** 关联的预算支出 ID */
    @Schema(description = "关联的预算支出 ID")
    private Long budgetExpenseId;

    /** 保修截止日期 */
    @Schema(description = "保修截止日期")
    private String warrantyEndDate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
