package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 区域列表（后台） 响应体。
 *
 * <p>对应接口：5-07（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatAreaListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 区域 ID */
    @Schema(description = "区域 ID")
    private Long areaId;

    /** 区域名称 */
    @Schema(description = "区域名称")
    private String name;

    /** 平面图底图地址（OSS） */
    @Schema(description = "平面图底图地址（OSS）")
    private String planImageUrl;

    /** 底图宽度（像素） */
    @Schema(description = "底图宽度（像素）")
    private Integer width;

    /** 底图高度（像素） */
    @Schema(description = "底图高度（像素）")
    private Integer height;

    /** 区域说明 */
    @Schema(description = "区域说明")
    private String description;

    /** 该区域下的工位数量 */
    @Schema(description = "该区域下的工位数量")
    private Integer seatCount;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortNo;
}
