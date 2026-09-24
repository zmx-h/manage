package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 修改资产分类 请求体。
 *
 * <p>对应接口：4-18（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetCategoryUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类 ID */
    @Schema(description = "分类 ID")
    @NotNull(message = "分类 ID不能为空")
    private Long categoryId;

    /** 分类名称，最长 64 字 */
    @Schema(description = "分类名称，最长 64 字")
    @NotBlank(message = "分类名称，最长 64 字不能为空")
    private String name;

    /** 父分类 ID，0 表示一级分类 */
    @Schema(description = "父分类 ID，0 表示一级分类")
    private Long parentId;

    /** 分类编码 */
    @Schema(description = "分类编码")
    private String code;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortNo;
}
