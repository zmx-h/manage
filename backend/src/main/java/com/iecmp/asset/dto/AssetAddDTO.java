package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "资产编号不能为空")
    @Size(max = 32, message = "资产编号长度不能超过 32 个字符")
    private String assetNo;

    /** 资产名称，最长 100 字 */
    @Schema(description = "资产名称，最长 100 字")
    @NotBlank(message = "资产名称不能为空")
    @Size(max = 100, message = "资产名称长度不能超过 100 个字符")
    private String name;

    /** 资产分类 ID */
    @Schema(description = "资产分类 ID")
    private Long categoryId;

    /** 品牌 */
    @Schema(description = "品牌")
    @Size(max = 64, message = "品牌长度不能超过 64 个字符")
    private String brand;

    /** 型号 */
    @Schema(description = "型号")
    @Size(max = 64, message = "型号长度不能超过 64 个字符")
    private String model;

    /** 购置价格 */
    @Schema(description = "购置价格")
    @DecimalMin(value = "0.00", message = "购置价格不能小于 0")
    @Digits(integer = 10, fraction = 2, message = "购置价格最多 10 位整数、2 位小数")
    private BigDecimal price;

    /** 购置日期，格式 yyyy-MM-dd */
    @Schema(description = "购置日期，格式 yyyy-MM-dd")
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$", message = "购置日期格式应为 yyyy-MM-dd")
    private String purchaseDate;

    /** 供应商 */
    @Schema(description = "供应商")
    @Size(max = 128, message = "供应商长度不能超过 128 个字符")
    private String supplier;

    /** 1 采购，2 捐赠，3 调拨 */
    @Schema(description = "1 采购，2 捐赠，3 调拨")
    @Min(value = 1, message = "资产来源只能为 1 采购、2 捐赠、3 调拨")
    @Max(value = 3, message = "资产来源只能为 1 采购、2 捐赠、3 调拨")
    private Integer sourceType;

    /** 关联的预算支出 ID；来源为采购时建议填写 */
    @Schema(description = "关联的预算支出 ID；来源为采购时建议填写")
    private Long budgetExpenseId;

    /** 保管人 ID */
    @Schema(description = "保管人 ID")
    private Long keeperId;

    /** 存放位置 */
    @Schema(description = "存放位置")
    @Size(max = 128, message = "存放位置长度不能超过 128 个字符")
    private String location;

    /** 资产照片地址数组，先调接口 8-01 上传后填入 */
    @Schema(description = "资产照片地址数组，先调接口 8-01 上传后填入")
    private List<String> imageUrls;

    /** 保修截止日期 */
    @Schema(description = "保修截止日期")
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$", message = "保修截止日期格式应为 yyyy-MM-dd")
    private String warrantyEndDate;

    /** 备注 */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
