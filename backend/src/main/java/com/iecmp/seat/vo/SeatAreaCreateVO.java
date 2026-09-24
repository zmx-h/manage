package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增区域（上传平面图） 响应体。
 *
 * <p>对应接口：5-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatAreaCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增区域 ID */
    @Schema(description = "新增区域 ID")
    private Long areaId;
}
