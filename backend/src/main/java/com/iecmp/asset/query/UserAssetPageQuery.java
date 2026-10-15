package com.iecmp.asset.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 资产分页（前台） 查询条件。
 *
 * <p>对应接口：4-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserAssetPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资产分类筛选（含子分类） */
    @Schema(description = "资产分类筛选（含子分类）")
    private Long categoryId;

    /** 1 在用，2 闲置，3 维修中，4 已借出，5 已报废 */
    @Schema(description = "1 在用，2 闲置，3 维修中，4 已借出，5 已报废")
    private Integer status;

    /** 模糊匹配 assetNo / name / brand / model */
    @Schema(description = "模糊匹配 assetNo / name / brand / model")
    private String keyword;
}
