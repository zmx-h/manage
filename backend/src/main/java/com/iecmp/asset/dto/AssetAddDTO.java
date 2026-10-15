package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 新增资产 请求体。
 *
 * <p>对应接口：4-10（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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

    /** 购置日期，格式 yyyy-MM-dd */
    @Schema(description = "购置日期，格式 yyyy-MM-dd")
    private String purchaseDate;

    /** 供应商 */
    @Schema(description = "供应商")
    private String supplier;

    /** 1 采购，2 捐赠，3 调拨 */
    @Schema(description = "1 采购，2 捐赠，3 调拨")
    private Integer sourceType;

    /** 关联的预算支出 ID；来源为采购时建议填写 */
    @Schema(description = "关联的预算支出 ID；来源为采购时建议填写")
    private Long budgetExpenseId;

    /** 保管人 ID */
    @Schema(description = "保管人 ID")
    private Long keeperId;

    /** 存放位置 */
    @Schema(description = "存放位置")
    private String location;

    /** 资产照片地址数组，先调接口 8-01 上传后填入 */
    @Schema(description = "资产照片地址数组，先调接口 8-01 上传后填入")
    private List<String> imageUrls;

    /** 保修截止日期 */
    @Schema(description = "保修截止日期")
    private String warrantyEndDate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
