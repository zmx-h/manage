package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 平面图数据 响应体。
 *
 * <p>对应接口：5-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserSeatPlanVO implements Serializable {

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

    /** 底图宽度（像素），坐标换算基准 */
    @Schema(description = "底图宽度（像素），坐标换算基准")
    private Integer width;

    /** 底图高度（像素），坐标换算基准 */
    @Schema(description = "底图高度（像素），坐标换算基准")
    private Integer height;

    /** 该区域下的工位数组，结构见下表 */
    @Schema(description = "该区域下的工位数组，结构见下表")
    private List<SeatVO> seats;

    /**
     * SeatVO：平面图数据 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class SeatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 工位 ID */
        @Schema(description = "工位 ID")
        private Long seatId;

        /** 工位编号，如 A-01 */
        @Schema(description = "工位编号，如 A-01")
        private String seatNo;

        /** 1 工位，2 会议室，3 储物柜，4 其他 */
        @Schema(description = "1 工位，2 会议室，3 储物柜，4 其他")
        private Integer seatType;

        /** X 坐标占底图宽度的百分比（0~100） */
        @Schema(description = "X 坐标占底图宽度的百分比（0~100）")
        private BigDecimal xRatio;

        /** Y 坐标占底图高度的百分比（0~100） */
        @Schema(description = "Y 坐标占底图高度的百分比（0~100）")
        private BigDecimal yRatio;

        /** 宽度占底图宽度的百分比 */
        @Schema(description = "宽度占底图宽度的百分比")
        private BigDecimal widthRatio;

        /** 高度占底图高度的百分比 */
        @Schema(description = "高度占底图高度的百分比")
        private BigDecimal heightRatio;

        /** 旋转角度（度） */
        @Schema(description = "旋转角度（度）")
        private BigDecimal rotation;

        /** 1 空闲，2 已占用，3 预留，4 维修中 */
        @Schema(description = "1 空闲，2 已占用，3 预留，4 维修中")
        private Integer status;

        /** 使用人姓名；空闲时为 null */
        @Schema(description = "使用人姓名；空闲时为 null")
        private String occupantName;

        /** 是否当前用户使用：0 否 1 是 */
        @Schema(description = "是否当前用户使用：0 否 1 是")
        private Integer isMine;
    }
}
