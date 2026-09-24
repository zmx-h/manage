package com.iecmp.seat.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 工位列表 查询条件。
 *
 * <p>对应接口：5-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatListQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 区域筛选 */
    @Schema(description = "区域筛选")
    private Long areaId;

    /** 工位类型筛选 */
    @Schema(description = "工位类型筛选")
    private Integer seatType;

    /** 状态筛选 */
    @Schema(description = "状态筛选")
    private Integer status;

    /** 模糊匹配 seatNo */
    @Schema(description = "模糊匹配 seatNo")
    private String keyword;
}
