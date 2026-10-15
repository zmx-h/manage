package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 工位状态变更 请求体。
 *
 * <p>对应接口：5-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标状态：1 空闲，3 预留，4 维修中 */
    @Schema(description = "目标状态：1 空闲，3 预留，4 维修中")
    @NotNull(message = "目标状态：1 空闲，3 预留，4 维修中不能为空")
    private Integer status;

    /** 变更原因 */
    @Schema(description = "变更原因")
    private String remark;
}
