package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的值日（含待办） 响应体。
 *
 * <p>对应接口：7-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyScheduleMyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 下次值日，结构见下表；无未来值日时为 null */
    @Schema(description = "下次值日，结构见下表；无未来值日时为 null")
    private NextDutyVO nextDuty;

    /** 今日值日；今日无值日时为 null */
    @Schema(description = "今日值日；今日无值日时为 null")
    private NextDutyVO todayDuty;

    /** 待完成（未确认）的值日次数 */
    @Schema(description = "待完成（未确认）的值日次数")
    private Integer pendingCount;

    /** 已完成的次数（当前计划内） */
    @Schema(description = "已完成的次数（当前计划内）")
    private Integer completedCount;

    /** 已换班的次数（当前计划内） */
    @Schema(description = "已换班的次数（当前计划内）")
    private Integer swappedCount;

    /** 待我处理的换班申请数 */
    @Schema(description = "待我处理的换班申请数")
    private Integer pendingSwapCount;

    /**
     * NextDutyVO：我的值日（含待办） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class NextDutyVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** scheduleId（字段含义见接口文档） */
        @Schema(description = "scheduleId（字段含义见接口文档）")
        private Long scheduleId;

        /** dutyDate（字段含义见接口文档） */
        @Schema(description = "dutyDate（字段含义见接口文档）")
        private String dutyDate;

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
