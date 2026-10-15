package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 值日日历（月视图） 响应体。
 *
 * <p>对应接口：7-03（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyScheduleCalendarVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 查询月份，格式 yyyy-MM */
    @Schema(description = "查询月份，格式 yyyy-MM")
    private String month;

    /** 排班计划 ID */
    @Schema(description = "排班计划 ID")
    private Long planId;

    /** 计划标题 */
    @Schema(description = "计划标题")
    private String planTitle;

    /** 日期数组，结构见下表 */
    @Schema(description = "日期数组，结构见下表")
    private List<DayVO> days;

    /** 当日值日安排，元素含 `scheduleId`、`userId`、`userName`、`groupName`、`taskContent`、`status` */
    @Schema(description = "当日值日安排，元素含 `scheduleId`、`userId`、`userName`、`groupName`、`taskContent`、`status`")
    private List<DutyVO> duties;

    /**
     * DayVO：值日日历（月视图） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class DayVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 日期，格式 yyyy-MM-dd */
        @Schema(description = "日期，格式 yyyy-MM-dd")
        private String date;

        /** 星期几：1 周一 ~ 7 周日 */
        @Schema(description = "星期几：1 周一 ~ 7 周日")
        private Integer weekday;

        /** 当日是否由当前用户值日 */
        @Schema(description = "当日是否由当前用户值日")
        private Integer isMine;
    }

    /**
     * DutyVO：值日日历（月视图） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class DutyVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** scheduleId（字段含义见接口文档） */
        @Schema(description = "scheduleId（字段含义见接口文档）")
        private Long scheduleId;

        /** userId（字段含义见接口文档） */
        @Schema(description = "userId（字段含义见接口文档）")
        private Long userId;

        /** userName（字段含义见接口文档） */
        @Schema(description = "userName（字段含义见接口文档）")
        private String userName;

        /** groupName（字段含义见接口文档） */
        @Schema(description = "groupName（字段含义见接口文档）")
        private String groupName;

        /** taskContent（字段含义见接口文档） */
        @Schema(description = "taskContent（字段含义见接口文档）")
        private String taskContent;

        /** status（字段含义见接口文档） */
        @Schema(description = "status（字段含义见接口文档）")
        private Integer status;
    }
}
