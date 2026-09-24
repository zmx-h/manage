package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 批量保存工位布局坐标 请求体。
 *
 * <p>对应接口：5-13（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatBatchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 区域 ID */
    @Schema(description = "区域 ID")
    @NotNull(message = "区域 ID不能为空")
    private Long areaId;

    /** 工位坐标数组，单次最多 200 项 */
    @Schema(description = "工位坐标数组，单次最多 200 项")
    @Valid
    @NotEmpty(message = "工位坐标数组，单次最多 200 项不能为空")
    private List<SeatDTO> seats;

    /**
     * SeatDTO：批量保存工位布局坐标 请求体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class SeatDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 工位 ID */
        @Schema(description = "工位 ID")
        private Long seatId;

        /** X 坐标百分比 */
        @Schema(description = "X 坐标百分比")
        private BigDecimal xRatio;

        /** Y 坐标百分比 */
        @Schema(description = "Y 坐标百分比")
        private BigDecimal yRatio;

        /** 宽度百分比 */
        @Schema(description = "宽度百分比")
        private BigDecimal widthRatio;

        /** 高度百分比 */
        @Schema(description = "高度百分比")
        private BigDecimal heightRatio;

        /** 旋转角度（度） */
        @Schema(description = "旋转角度（度）")
        private BigDecimal rotation;
    }
}
