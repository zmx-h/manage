package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "科目名称，最长 64 字不能为空")
    private String name;

    /** 科目分类 */
    @Schema(description = "科目分类")
    private Integer category;

    /** 科目预算额度 */
    @Schema(description = "科目预算额度")
    @NotNull(message = "科目预算额度不能为空")
    private BigDecimal budgetAmount;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortNo;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
