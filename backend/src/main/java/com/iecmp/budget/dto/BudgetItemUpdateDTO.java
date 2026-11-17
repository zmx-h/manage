package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 修改预算科目 请求体。
 *
 * <p>对应接口：3-18（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetItemUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 科目 ID */
    @Schema(description = "科目 ID")
    @NotNull(message = "科目 ID不能为空")
    private Long itemId;

    /** 科目名称，最长 64 字 */
    @Schema(description = "科目名称，最长 64 字")
    @NotBlank(message = "科目名称不能为空")
    @Size(max = 64, message = "科目名称最长 64 字")
    private String name;

    /** 1 设备采购，2 耗材，3 活动，4 差旅，5 其他 */
    @Schema(description = "1 设备采购，2 耗材，3 活动，4 差旅，5 其他")
    @Min(value = 1, message = "科目分类取值范围 1~5")
    @Max(value = 5, message = "科目分类取值范围 1~5")
    private Integer category;

    /** 科目预算额度，不得低于已使用额度 */
    @Schema(description = "科目预算额度，不得低于已使用额度")
    @NotNull(message = "科目预算额度不能为空")
    @Positive(message = "科目预算额度必须大于 0")
    private BigDecimal budgetAmount;

    /** 排序号，升序 */
    @Schema(description = "排序号，升序")
    private Integer sortNo;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
