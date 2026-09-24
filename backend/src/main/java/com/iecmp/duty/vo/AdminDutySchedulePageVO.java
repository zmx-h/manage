package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 值日安排分页（后台） 响应体。
 *
 * <p>对应接口：7-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminDutySchedulePageVO implements Serializable {

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

    /** 年级 */
    @Schema(description = "年级")
    private String grade;

    /** 分组名称 */
    @Schema(description = "分组名称")
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

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 该条目的换班次数 */
    @Schema(description = "该条目的换班次数")
    private Integer swapCount;
}
