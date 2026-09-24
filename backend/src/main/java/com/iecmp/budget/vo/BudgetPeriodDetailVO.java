package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 预算周期详情 响应体。
 *
 * <p>对应接口：3-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetPeriodDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 预算周期 ID */
    @Schema(description = "预算周期 ID")
    private Long periodId;

    /** 周期标题 */
    @Schema(description = "周期标题")
    private String title;

    /** 所属年度 */
    @Schema(description = "所属年度")
    private String year;

    /** 预算总额度 */
    @Schema(description = "预算总额度")
    private BigDecimal totalAmount;

    /** 已使用额度 */
    @Schema(description = "已使用额度")
    private BigDecimal usedAmount;

    /** 剩余额度 */
    @Schema(description = "剩余额度")
    private BigDecimal remainAmount;

    /** 执行率（百分比数值） */
    @Schema(description = "执行率（百分比数值）")
    private BigDecimal executionRate;

    /** 周期开始日期 */
    @Schema(description = "周期开始日期")
    private String startDate;

    /** 周期结束日期 */
    @Schema(description = "周期结束日期")
    private String endDate;

    /** 周期说明 */
    @Schema(description = "周期说明")
    private String description;

    /** 0 草稿，1 执行中，2 已结束 */
    @Schema(description = "0 草稿，1 执行中，2 已结束")
    private Integer status;

    /** 科目数量 */
    @Schema(description = "科目数量")
    private Integer itemCount;

    /** 该周期下的申请总数 */
    @Schema(description = "该周期下的申请总数")
    private Integer applyCount;

    /** 该周期下的支出笔数 */
    @Schema(description = "该周期下的支出笔数")
    private Integer expenseCount;

    /** 公示时间 */
    @Schema(description = "公示时间")
    private String publicTime;
}
