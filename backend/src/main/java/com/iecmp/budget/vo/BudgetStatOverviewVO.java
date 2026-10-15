package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 预算执行看板 响应体。
 *
 * <p>对应接口：3-25（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetStatOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 预算周期 ID */
    @Schema(description = "预算周期 ID")
    private Long periodId;

    /** 周期标题 */
    @Schema(description = "周期标题")
    private String periodTitle;

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

    /** 科目维度统计，结构见下表 */
    @Schema(description = "科目维度统计，结构见下表")
    private List<ItemStatVO> itemStats;

    /** 申请统计，含 `pendingCount`、`approvedCount`、`rejectedCount`、`approvedAmount` */
    @Schema(description = "申请统计，含 `pendingCount`、`approvedCount`、`rejectedCount`、`approvedAmount`")
    private ApplyStatVO applyStats;

    /** 月度支出趋势，元素含 `month`（yyyy-MM）、`amount` */
    @Schema(description = "月度支出趋势，元素含 `month`（yyyy-MM）、`amount`")
    private List<MonthTrendVO> monthTrend;

    /**
     * ItemStatVO：预算执行看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class ItemStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 科目 ID */
        @Schema(description = "科目 ID")
        private Long itemId;

        /** 科目名称 */
        @Schema(description = "科目名称")
        private String itemName;

        /** 科目分类 */
        @Schema(description = "科目分类")
        private Integer category;

        /** 科目预算额度 */
        @Schema(description = "科目预算额度")
        private BigDecimal budgetAmount;

        /** 已使用额度 */
        @Schema(description = "已使用额度")
        private BigDecimal usedAmount;

        /** 执行率（百分比数值） */
        @Schema(description = "执行率（百分比数值）")
        private BigDecimal executionRate;
    }

    /**
     * ApplyStatVO：预算执行看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class ApplyStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** pendingCount（字段含义见接口文档） */
        @Schema(description = "pendingCount（字段含义见接口文档）")
        private Integer pendingCount;

        /** approvedCount（字段含义见接口文档） */
        @Schema(description = "approvedCount（字段含义见接口文档）")
        private Integer approvedCount;

        /** rejectedCount（字段含义见接口文档） */
        @Schema(description = "rejectedCount（字段含义见接口文档）")
        private Integer rejectedCount;

        /** approvedAmount（字段含义见接口文档） */
        @Schema(description = "approvedAmount（字段含义见接口文档）")
        private BigDecimal approvedAmount;
    }

    /**
     * MonthTrendVO：预算执行看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class MonthTrendVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** month（字段含义见接口文档） */
        @Schema(description = "month（字段含义见接口文档）")
        private String month;

        /** amount（字段含义见接口文档） */
        @Schema(description = "amount（字段含义见接口文档）")
        private String amount;
    }
}
