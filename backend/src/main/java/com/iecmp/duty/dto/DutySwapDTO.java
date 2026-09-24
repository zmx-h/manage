package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 发起换班申请 请求体。
 *
 * <p>对应接口：7-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutySwapDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请方自己的值日安排 ID */
    @Schema(description = "申请方自己的值日安排 ID")
    @NotNull(message = "申请方自己的值日安排 ID不能为空")
    private Long scheduleId;

    /** 换班对象（希望接替的人）的用户 ID */
    @Schema(description = "换班对象（希望接替的人）的用户 ID")
    @NotNull(message = "换班对象（希望接替的人）的用户 ID不能为空")
    private Long targetUserId;

    /** 对调的目标值日安排 ID；不传表示「请对方单向代我值日」 */
    @Schema(description = "对调的目标值日安排 ID；不传表示「请对方单向代我值日」")
    private Long targetScheduleId;

    /** 换班理由，最长 500 字 */
    @Schema(description = "换班理由，最长 500 字")
    private String reason;
}
