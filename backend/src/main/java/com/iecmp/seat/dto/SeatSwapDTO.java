package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 成员发起座位调换 请求体。
 *
 * <p>对应接口：5-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatSwapDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 对调方成员的用户 ID */
    @Schema(description = "对调方成员的用户 ID")
    @NotNull(message = "对调方成员的用户 ID不能为空")
    private Long targetUserId;

    /** 调换理由，最长 500 字 */
    @Schema(description = "调换理由，最长 500 字")
    private String reason;
}
