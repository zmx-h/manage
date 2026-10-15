package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 分配工位 响应体。
 *
 * <p>对应接口：5-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatAssignVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 工位 ID */
    @Schema(description = "工位 ID")
    private Long seatId;

    /** 被自动释放的原工位 ID；无原工位时为 null */
    @Schema(description = "被自动释放的原工位 ID；无原工位时为 null")
    private Long releasedSeatId;
}
