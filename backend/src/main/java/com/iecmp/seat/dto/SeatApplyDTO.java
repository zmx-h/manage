package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交工位申请 请求体。
 *
 * <p>对应接口：5-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 意向区域 ID */
    @Schema(description = "意向区域 ID")
    @NotNull(message = "意向区域 ID不能为空")
    private Long areaId;

    /** 意向工位 ID；不传表示仅申请意向区域，由负责人分配 */
    @Schema(description = "意向工位 ID；不传表示仅申请意向区域，由负责人分配")
    private Long seatId;

    /** 申请理由，最长 500 字 */
    @Schema(description = "申请理由，最长 500 字")
    private String reason;
}
