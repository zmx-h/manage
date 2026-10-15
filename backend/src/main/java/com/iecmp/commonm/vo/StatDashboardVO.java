package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 后台统计看板 响应体。
 *
 * <p>对应接口：8-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class StatDashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 启用状态的成员数 */
    @Schema(description = "启用状态的成员数")
    private Integer userCount;

    /** 资产统计，含 `total`、`inUse`、`idle`、`repair`、`scrapped` */
    @Schema(description = "资产统计，含 `total`、`inUse`、`idle`、`repair`、`scrapped`")
    private AssetStatVO assetStat;

    /** 当前执行周期统计，含 `periodTitle`、`totalAmount`、`usedAmount`、`executionRate` */
    @Schema(description = "当前执行周期统计，含 `periodTitle`、`totalAmount`、`usedAmount`、`executionRate`")
    private BudgetStatVO budgetStat;

    /** 今日值日人员，元素含 `userId`、`realName`、`groupName`、`status` */
    @Schema(description = "今日值日人员，元素含 `userId`、`realName`、`groupName`、`status`")
    private List<TodayDutyVO> todayDuty;

    /** 全中心本月提交次数 */
    @Schema(description = "全中心本月提交次数")
    private Integer repoCommitCountThisMonth;

    /** 待办数量聚合，含 `budgetApply`、`assetBorrow`、`seatApply`、`seatSwap`、`honorAudit`、`registerApply` */
    @Schema(description = "待办数量聚合，含 `budgetApply`、`assetBorrow`、`seatApply`、`seatSwap`、`honorAudit`、`registerApply`")
    private PendingTodoVO pendingTodo;

    /**
     * AssetStatVO：后台统计看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class AssetStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** total（字段含义见接口文档） */
        @Schema(description = "total（字段含义见接口文档）")
        private Integer total;

        /** inUse（字段含义见接口文档） */
        @Schema(description = "inUse（字段含义见接口文档）")
        private Integer inUse;

        /** idle（字段含义见接口文档） */
        @Schema(description = "idle（字段含义见接口文档）")
        private Integer idle;

        /** repair（字段含义见接口文档） */
        @Schema(description = "repair（字段含义见接口文档）")
        private Integer repair;

        /** scrapped（字段含义见接口文档） */
        @Schema(description = "scrapped（字段含义见接口文档）")
        private Integer scrapped;
    }

    /**
     * BudgetStatVO：后台统计看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class BudgetStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** periodTitle（字段含义见接口文档） */
        @Schema(description = "periodTitle（字段含义见接口文档）")
        private String periodTitle;

        /** totalAmount（字段含义见接口文档） */
        @Schema(description = "totalAmount（字段含义见接口文档）")
        private BigDecimal totalAmount;

        /** usedAmount（字段含义见接口文档） */
        @Schema(description = "usedAmount（字段含义见接口文档）")
        private BigDecimal usedAmount;

        /** executionRate（字段含义见接口文档） */
        @Schema(description = "executionRate（字段含义见接口文档）")
        private BigDecimal executionRate;
    }

    /**
     * TodayDutyVO：后台统计看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class TodayDutyVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** userId（字段含义见接口文档） */
        @Schema(description = "userId（字段含义见接口文档）")
        private Long userId;

        /** realName（字段含义见接口文档） */
        @Schema(description = "realName（字段含义见接口文档）")
        private String realName;

        /** groupName（字段含义见接口文档） */
        @Schema(description = "groupName（字段含义见接口文档）")
        private String groupName;

        /** status（字段含义见接口文档） */
        @Schema(description = "status（字段含义见接口文档）")
        private Integer status;
    }

    /**
     * PendingTodoVO：后台统计看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class PendingTodoVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** budgetApply（字段含义见接口文档） */
        @Schema(description = "budgetApply（字段含义见接口文档）")
        private Integer budgetApply;

        /** assetBorrow（字段含义见接口文档） */
        @Schema(description = "assetBorrow（字段含义见接口文档）")
        private Integer assetBorrow;

        /** seatApply（字段含义见接口文档） */
        @Schema(description = "seatApply（字段含义见接口文档）")
        private Integer seatApply;

        /** seatSwap（字段含义见接口文档） */
        @Schema(description = "seatSwap（字段含义见接口文档）")
        private Integer seatSwap;

        /** honorAudit（字段含义见接口文档） */
        @Schema(description = "honorAudit（字段含义见接口文档）")
        private Integer honorAudit;

        /** registerApply（字段含义见接口文档） */
        @Schema(description = "registerApply（字段含义见接口文档）")
        private Integer registerApply;
    }
}
