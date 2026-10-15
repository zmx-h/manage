package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 成员发起座位调换 响应体。
 *
 * <p>对应接口：5-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatSwapVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 调换申请 ID */
    @Schema(description = "调换申请 ID")
    private Long swapId;

    /** 申请方当前工位 ID */
    @Schema(description = "申请方当前工位 ID")
    private Long applySeatId;

    /** 对调方当前工位 ID */
    @Schema(description = "对调方当前工位 ID")
    private Long targetSeatId;
}
