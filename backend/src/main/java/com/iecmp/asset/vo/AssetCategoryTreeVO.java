package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 资产分类树 响应体。
 *
 * <p>对应接口：4-03（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetCategoryTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类 ID */
    @Schema(description = "分类 ID")
    private Long categoryId;

    /** 分类名称 */
    @Schema(description = "分类名称")
    private String name;

    /** 父分类 ID，0 表示一级分类 */
    @Schema(description = "父分类 ID，0 表示一级分类")
    private Long parentId;

    /** 该分类下的资产数量（含子分类） */
    @Schema(description = "该分类下的资产数量（含子分类）")
    private Integer assetCount;

    /** 子分类 */
    @Schema(description = "子分类")
    private List<String> children;
}
