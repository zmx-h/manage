package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 新增预算周期（起草） 请求体。
 *
 * <p>对应接口：3-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetPeriodAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 周期标题，最长 100 字 */
    @Schema(description = "周期标题，最长 100 字")
    @NotBlank(message = "周期标题，最长 100 字不能为空")
    private String title;

    /** 所属年度，格式 yyyy */
    @Schema(description = "所属年度，格式 yyyy")
    @NotBlank(message = "所属年度，格式 yyyy不能为空")
    private String year;

    /** 预算总额度，必须大于 0 */
    @Schema(description = "预算总额度，必须大于 0")
    @NotNull(message = "预算总额度，必须大于 0不能为空")
    private BigDecimal totalAmount;

    /** 周期开始日期，格式 yyyy-MM-dd */
    @Schema(description = "周期开始日期，格式 yyyy-MM-dd")
    private String startDate;

    /** 周期结束日期，需晚于开始日期 */
    @Schema(description = "周期结束日期，需晚于开始日期")
    private String endDate;

    /** 周期说明，最长 500 字 */
    @Schema(description = "周期说明，最长 500 字")
    private String description;
}
