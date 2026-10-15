package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 结束盘点 响应体。
 *
 * <p>对应接口：4-26（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetInventoryFinishVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 盘点任务 ID */
    @Schema(description = "盘点任务 ID")
    private Long inventoryId;

    /** 应盘点总数 */
    @Schema(description = "应盘点总数")
    private Integer totalCount;

    /** 正常数量 */
    @Schema(description = "正常数量")
    private Integer normalCount;

    /** 盘亏数量 */
    @Schema(description = "盘亏数量")
    private Integer lossCount;

    /** 位置不符数量 */
    @Schema(description = "位置不符数量")
    private Integer mismatchCount;

    /** 未盘点数量 */
    @Schema(description = "未盘点数量")
    private Integer uncheckedCount;

    /** 异常明细列表，元素含 `assetId`、`assetNo`、`assetName`、`checkStatus`、`remark` */
    @Schema(description = "异常明细列表，元素含 `assetId`、`assetNo`、`assetName`、`checkStatus`、`remark`")
    private List<AbnormalListVO> abnormalList;

    /**
     * AbnormalListVO：结束盘点 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class AbnormalListVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** assetId（字段含义见接口文档） */
        @Schema(description = "assetId（字段含义见接口文档）")
        private Long assetId;

        /** assetNo（字段含义见接口文档） */
        @Schema(description = "assetNo（字段含义见接口文档）")
        private String assetNo;

        /** assetName（字段含义见接口文档） */
        @Schema(description = "assetName（字段含义见接口文档）")
        private String assetName;

        /** checkStatus（字段含义见接口文档） */
        @Schema(description = "checkStatus（字段含义见接口文档）")
        private Integer checkStatus;

        /** remark（字段含义见接口文档） */
        @Schema(description = "remark（字段含义见接口文档）")
        private String remark;
    }
}
