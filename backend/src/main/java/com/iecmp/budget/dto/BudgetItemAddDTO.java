package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 新增预算科目 请求体。
 *
 * <p>对应接口：3-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetItemAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 所属预算周期 ID */
    @Schema(description = "所属预算周期 ID")
    @NotNull(message = "所属预算周期 ID不能为空")
    private Long periodId;

    /** 科目名称，最长 64 字 */
    @Schema(description = "科目名称，最长 64 字")
    @NotBlank(message = "科目名称，最长 64 字不能为空")
    @Size(max = 64, message = "科目名称最长 64 字")
    private String name;

    /** 1 设备采购，2 耗材，3 活动，4 差旅，5 其他 */
    @Schema(description = "1 设备采购，2 耗材，3 活动，4 差旅，5 其他")
    @Min(value = 1, message = "科目分类仅支持 1 ~ 5")
    @Max(value = 5, message = "科目分类仅支持 1 ~ 5")
    private Integer category;

    /** 科目预算额度，必须大于 0 */
    @Schema(description = "科目预算额度，必须大于 0")
    @NotNull(message = "科目预算额度，必须大于 0不能为空")
    @DecimalMin(value = "0.01", message = "科目预算额度必须大于 0")
    @Digits(integer = 10, fraction = 2, message = "科目预算额度最多 10 位整数、2 位小数")
    private BigDecimal budgetAmount;

    /** 排序号，升序 */
    @Schema(description = "排序号，升序")
    private Integer sortNo;

    /** 备注 */
    @Schema(description = "备注")
    @Size(max = 255, message = "备注最长 255 字")
    private String remark;
}
