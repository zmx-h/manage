package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 平面图数据（后台） 响应体。
 *
 * <p>对应接口：5-25（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminSeatPlanVO implements Serializable {

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

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortNo;

    /** 工位统计，含 `total`、`free`、`used`、`reserved`、`repairing` */
    @Schema(description = "工位统计，含 `total`、`free`、`used`、`reserved`、`repairing`")
    private SeatStatVO seatStat;

    /** 该区域下的工位数组，结构见下表 */
    @Schema(description = "该区域下的工位数组，结构见下表")
    private List<SeatVO> seats;

    /**
     * SeatStatVO：平面图数据（后台） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class SeatStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** total（字段含义见接口文档） */
        @Schema(description = "total（字段含义见接口文档）")
        private Integer total;

        /** free（字段含义见接口文档） */
        @Schema(description = "free（字段含义见接口文档）")
        private Integer free;

        /** used（字段含义见接口文档） */
        @Schema(description = "used（字段含义见接口文档）")
        private Integer used;

        /** reserved（字段含义见接口文档） */
        @Schema(description = "reserved（字段含义见接口文档）")
        private Integer reserved;

        /** repairing（字段含义见接口文档） */
        @Schema(description = "repairing（字段含义见接口文档）")
        private Integer repairing;
    }

    /**
     * SeatVO：平面图数据（后台） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class SeatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 工位 ID */
        @Schema(description = "工位 ID")
        private Long seatId;

        /** 工位编号 */
        @Schema(description = "工位编号")
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

        /** 使用人用户 ID；空闲时为 null */
        @Schema(description = "使用人用户 ID；空闲时为 null")
        private Long occupantUserId;

        /** 使用人姓名 */
        @Schema(description = "使用人姓名")
        private String occupantName;

        /** 使用人年级，便于管理员核对 */
        @Schema(description = "使用人年级，便于管理员核对")
        private String occupantGrade;

        /** 使用人专业 */
        @Schema(description = "使用人专业")
        private String occupantMajor;

        /** 分配时间 */
        @Schema(description = "分配时间")
        private String assignTime;

        /** 工位备注 */
        @Schema(description = "工位备注")
        private String remark;
    }
}
