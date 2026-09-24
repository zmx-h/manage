package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 修改工位 请求体。
 *
 * <p>对应接口：5-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 工位 ID */
    @Schema(description = "工位 ID")
    @NotNull(message = "工位 ID不能为空")
    private Long seatId;

    /** 所属区域 ID */
    @Schema(description = "所属区域 ID")
    @NotNull(message = "所属区域 ID不能为空")
    private Long areaId;

    /** 工位编号，同一区域内唯一 */
    @Schema(description = "工位编号，同一区域内唯一")
    @NotBlank(message = "工位编号，同一区域内唯一不能为空")
    private String seatNo;

    /** 1 工位，2 会议室，3 储物柜，4 其他 */
    @Schema(description = "1 工位，2 会议室，3 储物柜，4 其他")
    private Integer seatType;

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

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
