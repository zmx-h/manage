package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 值日统计看板 响应体。
 *
 * <p>对应接口：7-19（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyStatOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排班计划 ID */
    @Schema(description = "排班计划 ID")
    private Long planId;

    /** 计划标题 */
    @Schema(description = "计划标题")
    private String planTitle;

    /** 值日条目总数 */
    @Schema(description = "值日条目总数")
    private Integer totalCount;

    /** 已完成数 */
    @Schema(description = "已完成数")
    private Integer completedCount;

    /** 未完成数（含已过期的） */
    @Schema(description = "未完成数（含已过期的）")
    private Integer unfinishedCount;

    /** 请假数 */
    @Schema(description = "请假数")
    private Integer leaveCount;

    /** 已换班数 */
    @Schema(description = "已换班数")
    private Integer swappedCount;

    /** 完成率（百分比数值） */
    @Schema(description = "完成率（百分比数值）")
    private BigDecimal completionRate;

    /** 已过期仍未完成的数量（`dutyDate < 今天` 且 `status = 0`） */
    @Schema(description = "已过期仍未完成的数量（`dutyDate < 今天` 且 `status = 0`）")
    private Integer overdueCount;

    /** 成员维度统计，结构见下表 */
    @Schema(description = "成员维度统计，结构见下表")
    private List<MemberStatVO> memberStats;

    /** 分组维度统计，元素含 `groupName`、`totalCount`、`completedCount`、`completionRate` */
    @Schema(description = "分组维度统计，元素含 `groupName`、`totalCount`、`completedCount`、`completionRate`")
    private List<GroupStatVO> groupStats;

    /**
     * MemberStatVO：值日统计看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class MemberStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 成员用户 ID */
        @Schema(description = "成员用户 ID")
        private Long userId;

        /** 成员姓名 */
        @Schema(description = "成员姓名")
        private String userName;

        /** 所属分组 */
        @Schema(description = "所属分组")
        private String groupName;

        /** 应值日次数 */
        @Schema(description = "应值日次数")
        private Integer totalCount;

        /** 已完成次数 */
        @Schema(description = "已完成次数")
        private Integer completedCount;

        /** 逾期未完成次数 */
        @Schema(description = "逾期未完成次数")
        private Integer overdueCount;

        /** 完成率（百分比数值） */
        @Schema(description = "完成率（百分比数值）")
        private BigDecimal completionRate;
    }

    /**
     * GroupStatVO：值日统计看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class GroupStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** groupName（字段含义见接口文档） */
        @Schema(description = "groupName（字段含义见接口文档）")
        private String groupName;

        /** totalCount（字段含义见接口文档） */
        @Schema(description = "totalCount（字段含义见接口文档）")
        private Integer totalCount;

        /** completedCount（字段含义见接口文档） */
        @Schema(description = "completedCount（字段含义见接口文档）")
        private Integer completedCount;

        /** completionRate（字段含义见接口文档） */
        @Schema(description = "completionRate（字段含义见接口文档）")
        private BigDecimal completionRate;
    }
}
