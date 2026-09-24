package com.iecmp.asset.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资产分页（后台） 查询条件。
 *
 * <p>对应接口：4-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminAssetPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类筛选（含子分类） */
    @Schema(description = "分类筛选（含子分类）")
    private Long categoryId;

    /** 资产状态 */
    @Schema(description = "资产状态")
    private Integer status;

    /** 1 采购，2 捐赠，3 调拨 */
    @Schema(description = "1 采购，2 捐赠，3 调拨")
    private Integer sourceType;

    /** 保管人筛选 */
    @Schema(description = "保管人筛选")
    private Long keeperId;

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
