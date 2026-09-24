package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 展板统计 响应体。
 *
 * <p>对应接口：6-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 展板人数（已通过且已上板） */
    @Schema(description = "展板人数（已通过且已上板）")
    private Integer alumniCount;

    /** 荣誉记录总数 */
    @Schema(description = "荣誉记录总数")
    private Integer honorCount;

    /** 毕业年份分布，元素含 `year`、`count` */
    @Schema(description = "毕业年份分布，元素含 `year`、`count`")
    private List<YearStatVO> yearStats;

    /** 荣誉级别分布，元素含 `level`、`count` */
    @Schema(description = "荣誉级别分布，元素含 `level`、`count`")
    private List<LevelStatVO> levelStats;

    /** 涉及的用人单位数量 */
    @Schema(description = "涉及的用人单位数量")
    private Integer companyCount;

    /**
     * YearStatVO：展板统计 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class YearStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** year（字段含义见接口文档） */
        @Schema(description = "year（字段含义见接口文档）")
        private String year;

        /** count（字段含义见接口文档） */
        @Schema(description = "count（字段含义见接口文档）")
        private String count;
    }

    /**
     * LevelStatVO：展板统计 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class LevelStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** level（字段含义见接口文档） */
        @Schema(description = "level（字段含义见接口文档）")
        private Integer level;

        /** count（字段含义见接口文档） */
        @Schema(description = "count（字段含义见接口文档）")
        private String count;
    }
}
