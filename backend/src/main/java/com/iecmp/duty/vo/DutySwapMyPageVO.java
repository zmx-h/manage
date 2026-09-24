package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的换班申请分页 响应体。
 *
 * <p>对应接口：7-06（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutySwapMyPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 换班申请 ID */
    @Schema(description = "换班申请 ID")
    private Long swapId;

    /** 申请方的值日安排 ID */
    @Schema(description = "申请方的值日安排 ID")
    private Long scheduleId;

    /** 申请人 ID */
    @Schema(description = "申请人 ID")
    private Long applyUserId;

    /** 申请人姓名 */
    @Schema(description = "申请人姓名")
    private String applyUserName;

    /** 申请方的值日日期 */
    @Schema(description = "申请方的值日日期")
    private String applyDutyDate;

    /** 换班对象 ID */
    @Schema(description = "换班对象 ID")
    private Long targetUserId;

    /** 换班对象姓名 */
    @Schema(description = "换班对象姓名")
    private String targetUserName;

    /** 对调的目标值日安排 ID；为空表示单向代值 */
    @Schema(description = "对调的目标值日安排 ID；为空表示单向代值")
    private Long targetScheduleId;

    /** 对调方的值日日期 */
    @Schema(description = "对调方的值日日期")
    private String targetDutyDate;

    /** 换班理由 */
    @Schema(description = "换班理由")
    private String reason;

    /** 0 待确认，1 已同意，2 已拒绝，3 已取消 */
    @Schema(description = "0 待确认，1 已同意，2 已拒绝，3 已取消")
    private Integer status;

    /** 当前用户是否为换班对象：0 否 1 是（决定是否展示「同意 / 拒绝」按钮） */
    @Schema(description = "当前用户是否为换班对象：0 否 1 是（决定是否展示「同意 / 拒绝」按钮）")
    private Integer isTarget;

    /** 处理时间 */
    @Schema(description = "处理时间")
    private String handleTime;

    /** 处理意见 */
    @Schema(description = "处理意见")
    private String handleRemark;

    /** 发起时间 */
    @Schema(description = "发起时间")
    private String createTime;
}
