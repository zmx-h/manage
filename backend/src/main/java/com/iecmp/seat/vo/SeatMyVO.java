package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 我的工位 响应体。
 *
 * <p>对应接口：5-03（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatMyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 工位 ID；未分配时为 null */
    @Schema(description = "工位 ID；未分配时为 null")
    private Long seatId;

    /** 工位编号 */
    @Schema(description = "工位编号")
    private String seatNo;

    /** 工位类型 */
    @Schema(description = "工位类型")
    private Integer seatType;

    /** 区域 ID */
    @Schema(description = "区域 ID")
    private Long areaId;

    /** 区域名称 */
    @Schema(description = "区域名称")
    private String areaName;

    /** 分配时间 */
    @Schema(description = "分配时间")
    private String assignTime;

    /** X 坐标百分比，便于前端在平面图上高亮 */
    @Schema(description = "X 坐标百分比，便于前端在平面图上高亮")
    private BigDecimal xRatio;

    /** Y 坐标百分比 */
    @Schema(description = "Y 坐标百分比")
    private BigDecimal yRatio;
}
