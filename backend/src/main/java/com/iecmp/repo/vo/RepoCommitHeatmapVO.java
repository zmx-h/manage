package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 贡献热力图 响应体。
 *
 * <p>对应接口：2-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoCommitHeatmapVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计年份 */
    @Schema(description = "统计年份")
    private String year;

    /** 全年提交总数 */
    @Schema(description = "全年提交总数")
    private Integer totalCommits;

    /** 有提交的天数（活跃天数） */
    @Schema(description = "有提交的天数（活跃天数）")
    private Integer activeDays;

    /** 单日最大提交数，用于前端计算色阶基准 */
    @Schema(description = "单日最大提交数，用于前端计算色阶基准")
    private Integer maxDaily;

    /** 每日数据点数组，结构见下表 */
    @Schema(description = "每日数据点数组，结构见下表")
    private List<CellVO> cells;

    /**
     * CellVO：贡献热力图 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class CellVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 日期，格式 yyyy-MM-dd */
        @Schema(description = "日期，格式 yyyy-MM-dd")
        private String date;

        /** 当日提交数 */
        @Schema(description = "当日提交数")
        private Integer count;

        /** 色阶等级 0~4，0 表示无提交；前端按等级着色（等级越高颜色越深） */
        @Schema(description = "色阶等级 0~4，0 表示无提交；前端按等级着色（等级越高颜色越深）")
        private Integer level;
    }
}
