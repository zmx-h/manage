package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增工位 响应体。
 *
 * <p>对应接口：5-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增工位 ID */
    @Schema(description = "新增工位 ID")
    private Long seatId;
}
