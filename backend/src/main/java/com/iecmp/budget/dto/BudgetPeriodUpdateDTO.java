package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 修改预算周期 请求体。
 *
 * <p>对应接口：3-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetPeriodUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 预算周期 ID */
    @Schema(description = "预算周期 ID")
    @NotNull(message = "预算周期 ID不能为空")
    private Long periodId;

    /** 周期标题，最长 100 字 */
    @Schema(description = "周期标题，最长 100 字")
    @NotBlank(message = "周期标题，最长 100 字不能为空")
    private String title;

    /** 所属年度 */
    @Schema(description = "所属年度")
    @NotBlank(message = "所属年度不能为空")
    private String year;

    /** 预算总额度 */
    @Schema(description = "预算总额度")
    @NotNull(message = "预算总额度不能为空")
    private BigDecimal totalAmount;

    /** 周期开始日期 */
    @Schema(description = "周期开始日期")
    private String startDate;

    /** 周期结束日期 */
    @Schema(description = "周期结束日期")
    private String endDate;

    /** 周期说明 */
    @Schema(description = "周期说明")
    private String description;
}
