package com.iecmp.asset.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 资产台账导出 查询条件。
 *
 * <p>对应接口：4-27（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetExportQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类筛选（含子分类） */
    @Schema(description = "分类筛选（含子分类）")
    private Long categoryId;

    /** 资产状态筛选 */
    @Schema(description = "资产状态筛选")
    private Integer status;

    /** 1 采购，2 捐赠，3 调拨 */
    @Schema(description = "1 采购，2 捐赠，3 调拨")
    private Integer sourceType;

    /** 模糊匹配 assetNo / name / brand / model */
    @Schema(description = "模糊匹配 assetNo / name / brand / model")
    private String keyword;

    /** 购置日期起 */
    @Schema(description = "购置日期起")
    private String beginDate;

    /** 购置日期止 */
    @Schema(description = "购置日期止")
    private String endDate;
}
