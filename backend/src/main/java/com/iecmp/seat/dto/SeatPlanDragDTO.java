package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 平面图拖拽调整（移动 / 互换） 请求体。
 *
 * <p>对应接口：5-26（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatPlanDragDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 被拖动的源工位 ID（当前有人使用） */
    @Schema(description = "被拖动的源工位 ID（当前有人使用）")
    @NotNull(message = "被拖动的源工位 ID（当前有人使用）不能为空")
    private Long fromSeatId;

    /** 拖动落点工位 ID：空工位则移动，已占用则互换 */
    @Schema(description = "拖动落点工位 ID：空工位则移动，已占用则互换")
    @NotNull(message = "拖动落点工位 ID：空工位则移动，已占用则互换不能为空")
    private Long toSeatId;

    /** 调整说明，如「按方向重新排座」，最长 255 字 */
    @Schema(description = "调整说明，如「按方向重新排座」，最长 255 字")
    @Size(max = 255, message = "调整说明不能超过 255 字")
    private String remark;
}
