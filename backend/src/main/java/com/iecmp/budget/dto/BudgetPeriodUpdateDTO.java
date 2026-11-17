package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "周期标题不能为空")
    @Size(max = 100, message = "周期标题最长 100 字")
    private String title;

    /** 所属年度，格式 yyyy */
    @Schema(description = "所属年度，格式 yyyy")
    @NotBlank(message = "所属年度不能为空")
    @Pattern(regexp = "^\\d{4}$", message = "所属年度格式必须为 yyyy")
    private String year;

    /** 预算总额度，不得低于已使用额度 */
    @Schema(description = "预算总额度，不得低于已使用额度")
    @NotNull(message = "预算总额度不能为空")
    @Positive(message = "预算总额度必须大于 0")
    private BigDecimal totalAmount;

    /** 周期开始日期，格式 yyyy-MM-dd */
    @Schema(description = "周期开始日期，格式 yyyy-MM-dd")
    private String startDate;

    /** 周期结束日期，需晚于开始日期 */
    @Schema(description = "周期结束日期，需晚于开始日期")
    private String endDate;

    /** 周期说明，最长 500 字 */
    @Schema(description = "周期说明，最长 500 字")
    @Size(max = 500, message = "周期说明最长 500 字")
    private String description;
}
