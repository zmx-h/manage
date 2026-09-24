package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 平面图拖拽调整（移动 / 互换） 响应体。
 *
 * <p>对应接口：5-26（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatPlanDragVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 本次动作：`move` 移动，`swap` 互换 */
    @Schema(description = "本次动作：`move` 移动，`swap` 互换")
    private String action;

    /** 源工位最新状态，含 `seatId`、`seatNo`、`status`、`occupantUserId`、`occupantName` */
    @Schema(description = "源工位最新状态，含 `seatId`、`seatNo`、`status`、`occupantUserId`、`occupantName`")
    private FromSeatVO fromSeat;

    /** 目标工位最新状态，字段同 `fromSeat` */
    @Schema(description = "目标工位最新状态，字段同 `fromSeat`")
    private FromSeatVO toSeat;

    /**
     * FromSeatVO：平面图拖拽调整（移动 / 互换） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class FromSeatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** seatId（字段含义见接口文档） */
        @Schema(description = "seatId（字段含义见接口文档）")
        private Long seatId;

        /** seatNo（字段含义见接口文档） */
        @Schema(description = "seatNo（字段含义见接口文档）")
        private String seatNo;

        /** status（字段含义见接口文档） */
        @Schema(description = "status（字段含义见接口文档）")
        private Integer status;

        /** occupantUserId（字段含义见接口文档） */
        @Schema(description = "occupantUserId（字段含义见接口文档）")
        private Long occupantUserId;

        /** occupantName（字段含义见接口文档） */
        @Schema(description = "occupantName（字段含义见接口文档）")
        private String occupantName;
    }
}
