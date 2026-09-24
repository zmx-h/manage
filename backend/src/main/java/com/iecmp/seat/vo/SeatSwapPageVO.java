package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 调换申请分页（后台） 响应体。
 *
 * <p>对应接口：5-23（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatSwapPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 调换申请 ID */
    @Schema(description = "调换申请 ID")
    private Long swapId;

    /** 申请方用户 ID */
    @Schema(description = "申请方用户 ID")
    private Long applyUserId;

    /** 申请方姓名 */
    @Schema(description = "申请方姓名")
    private String applyUserName;

    /** 申请方年级 */
    @Schema(description = "申请方年级")
    private String applyUserGrade;

    /** 申请方工位 ID */
    @Schema(description = "申请方工位 ID")
    private Long applySeatId;

    /** 申请方工位编号 */
    @Schema(description = "申请方工位编号")
    private String applySeatNo;

    /** 对调方用户 ID */
    @Schema(description = "对调方用户 ID")
    private Long targetUserId;

    /** 对调方姓名 */
    @Schema(description = "对调方姓名")
    private String targetUserName;

    /** 对调方年级 */
    @Schema(description = "对调方年级")
    private String targetUserGrade;

    /** 对调方工位 ID */
    @Schema(description = "对调方工位 ID")
    private Long targetSeatId;

    /** 对调方工位编号 */
    @Schema(description = "对调方工位编号")
    private String targetSeatNo;

    /** 调换理由 */
    @Schema(description = "调换理由")
    private String reason;

    /** 0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消 */
    @Schema(description = "0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消")
    private Integer status;

    /** 对方处理时间 */
    @Schema(description = "对方处理时间")
    private String handleTime;

    /** 对方处理意见 */
    @Schema(description = "对方处理意见")
    private String handleRemark;

    /** 审核人 ID */
    @Schema(description = "审核人 ID")
    private Long auditUserId;

    /** 审核人姓名 */
    @Schema(description = "审核人姓名")
    private String auditUserName;

    /** 审核时间 */
    @Schema(description = "审核时间")
    private String auditTime;

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;

    /** 发起时间 */
    @Schema(description = "发起时间")
    private String createTime;
}
