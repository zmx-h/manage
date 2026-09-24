package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 工位列表 响应体。
 *
 * <p>对应接口：5-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 工位 ID */
    @Schema(description = "工位 ID")
    private Long seatId;

    /** 所属区域 ID */
    @Schema(description = "所属区域 ID")
    private Long areaId;

    /** 区域名称 */
    @Schema(description = "区域名称")
    private String areaName;

    /** 工位编号 */
    @Schema(description = "工位编号")
    private String seatNo;

    /** 1 工位，2 会议室，3 储物柜，4 其他 */
    @Schema(description = "1 工位，2 会议室，3 储物柜，4 其他")
    private Integer seatType;

    /** 1 空闲，2 已占用，3 预留，4 维修中 */
    @Schema(description = "1 空闲，2 已占用，3 预留，4 维修中")
    private Integer status;

    /** 使用人姓名 */
    @Schema(description = "使用人姓名")
    private String occupantName;

    /** 分配时间 */
    @Schema(description = "分配时间")
    private String assignTime;
}
