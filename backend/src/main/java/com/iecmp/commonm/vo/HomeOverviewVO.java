package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 前台首页看板 响应体。
 *
 * <p>对应接口：8-10（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HomeOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 我的工位，含 `seatId`、`seatNo`、`areaName`；未分配时返回 null */
    @Schema(description = "我的工位，含 `seatId`、`seatNo`、`areaName`；未分配时返回 null")
    private MySeatVO mySeat;

    /** 我的下次值日，含 `dutyDate`、`taskContent`、`groupName` */
    @Schema(description = "我的下次值日，含 `dutyDate`、`taskContent`、`groupName`")
    private NextDutyVO nextDuty;

    /** 我当前借用中（未归还）的资产数量 */
    @Schema(description = "我当前借用中（未归还）的资产数量")
    private Integer borrowingCount;

    /** 我待审核的预算申请数 */
    @Schema(description = "我待审核的预算申请数")
    private Integer budgetApplyPending;

    /** 我本年度已通过的申请总金额 */
    @Schema(description = "我本年度已通过的申请总金额")
    private BigDecimal budgetApprovedAmount;

    /** 我本月提交次数 */
    @Schema(description = "我本月提交次数")
    private Integer commitCountThisMonth;

    /** 我的未读站内消息数（见 8-21） */
    @Schema(description = "我的未读站内消息数（见 8-21）")
    private Integer unreadMsgCount;

    /** 置顶公告列表，元素含 `noticeId`、`title`、`publishTime`，最多 5 条 */
    @Schema(description = "置顶公告列表，元素含 `noticeId`、`title`、`publishTime`，最多 5 条")
    private List<TopNoticeVO> topNotices;

    /**
     * MySeatVO：前台首页看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class MySeatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** seatId（字段含义见接口文档） */
        @Schema(description = "seatId（字段含义见接口文档）")
        private Long seatId;

        /** seatNo（字段含义见接口文档） */
        @Schema(description = "seatNo（字段含义见接口文档）")
        private String seatNo;

        /** areaName（字段含义见接口文档） */
        @Schema(description = "areaName（字段含义见接口文档）")
        private String areaName;
    }

    /**
     * NextDutyVO：前台首页看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class NextDutyVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** dutyDate（字段含义见接口文档） */
        @Schema(description = "dutyDate（字段含义见接口文档）")
        private String dutyDate;

        /** taskContent（字段含义见接口文档） */
        @Schema(description = "taskContent（字段含义见接口文档）")
        private String taskContent;

        /** groupName（字段含义见接口文档） */
        @Schema(description = "groupName（字段含义见接口文档）")
        private String groupName;
    }

    /**
     * TopNoticeVO：前台首页看板 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class TopNoticeVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** noticeId（字段含义见接口文档） */
        @Schema(description = "noticeId（字段含义见接口文档）")
        private Long noticeId;

        /** title（字段含义见接口文档） */
        @Schema(description = "title（字段含义见接口文档）")
        private String title;

        /** publishTime（字段含义见接口文档） */
        @Schema(description = "publishTime（字段含义见接口文档）")
        private String publishTime;
    }
}
