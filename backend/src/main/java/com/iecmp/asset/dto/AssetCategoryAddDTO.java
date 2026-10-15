package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增资产分类 请求体。
 *
 * <p>对应接口：4-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetCategoryAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类名称，最长 64 字 */
    @Schema(description = "分类名称，最长 64 字")
    @NotBlank(message = "分类名称，最长 64 字不能为空")
    private String name;

    /** 父分类 ID，0 表示一级分类 */
    @Schema(description = "父分类 ID，0 表示一级分类")
    private Long parentId;

    /** 分类编码，全局唯一 */
    @Schema(description = "分类编码，全局唯一")
    private String code;

    /** 排序号，升序 */
    @Schema(description = "排序号，升序")
    private Integer sortNo;
}
