package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交工位申请 响应体。
 *
 * <p>对应接口：5-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatApplyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 工位申请 ID */
    @Schema(description = "工位申请 ID")
    private Long applyId;
}
