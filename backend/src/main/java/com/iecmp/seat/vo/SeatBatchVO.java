package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 批量保存工位布局坐标 响应体。
 *
 * <p>对应接口：5-13（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatBatchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 实际更新的工位数 */
    @Schema(description = "实际更新的工位数")
    private Integer updatedCount;
}
