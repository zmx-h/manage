package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 值日安排分页（公示） 响应体。
 *
 * <p>对应接口：7-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserDutySchedulePageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 值日安排 ID */
    @Schema(description = "值日安排 ID")
    private Long scheduleId;

    /** 所属计划 ID */
    @Schema(description = "所属计划 ID")
    private Long planId;

    /** 计划标题 */
    @Schema(description = "计划标题")
    private String planTitle;

    /** 值日日期 */
    @Schema(description = "值日日期")
    private String dutyDate;

    /** 值日人 ID */
    @Schema(description = "值日人 ID")
    private Long userId;

    /** 值日人姓名 */
    @Schema(description = "值日人姓名")
    private String userName;

    /** 所属分组名称 */
    @Schema(description = "所属分组名称")
    private String groupName;

    /** 值日内容 */
    @Schema(description = "值日内容")
    private String taskContent;

    /** 0 未完成，1 已完成，2 请假，3 已换班 */
    @Schema(description = "0 未完成，1 已完成，2 请假，3 已换班")
    private Integer status;

    /** 完成确认时间 */
    @Schema(description = "完成确认时间")
    private String confirmTime;

    /** 是否当前用户的值日：0 否 1 是（决定是否展示「确认完成」按钮） */
    @Schema(description = "是否当前用户的值日：0 否 1 是（决定是否展示「确认完成」按钮）")
    private Integer isMine;
}
