package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 资产总览统计 响应体。
 *
 * <p>对应接口：4-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetStatOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资产总数 */
    @Schema(description = "资产总数")
    private Integer total;

    /** 资产总价值 */
    @Schema(description = "资产总价值")
    private BigDecimal totalValue;

    /** 在用数量 */
    @Schema(description = "在用数量")
    private Integer inUse;

    /** 闲置数量 */
    @Schema(description = "闲置数量")
    private Integer idle;

    /** 维修中数量 */
    @Schema(description = "维修中数量")
    private Integer repair;

    /** 已借出数量 */
    @Schema(description = "已借出数量")
    private Integer borrowed;

    /** 已报废数量 */
    @Schema(description = "已报废数量")
    private Integer scrapped;

    /** 分类维度统计，元素含 `categoryName`、`count` */
    @Schema(description = "分类维度统计，元素含 `categoryName`、`count`")
    private List<CategoryStatVO> categoryStats;

    /**
     * CategoryStatVO：资产总览统计 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class CategoryStatVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** categoryName（字段含义见接口文档） */
        @Schema(description = "categoryName（字段含义见接口文档）")
        private String categoryName;

        /** count（字段含义见接口文档） */
        @Schema(description = "count（字段含义见接口文档）")
        private String count;
    }
}
