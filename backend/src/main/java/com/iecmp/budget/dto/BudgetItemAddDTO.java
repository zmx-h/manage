package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String name;

    /** 1 设备采购，2 耗材，3 活动，4 差旅，5 其他 */
    @Schema(description = "1 设备采购，2 耗材，3 活动，4 差旅，5 其他")
    private Integer category;

    /** 科目预算额度，必须大于 0 */
    @Schema(description = "科目预算额度，必须大于 0")
    @NotNull(message = "科目预算额度，必须大于 0不能为空")
    private BigDecimal budgetAmount;

    /** 排序号，升序 */
    @Schema(description = "排序号，升序")
    private Integer sortNo;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
