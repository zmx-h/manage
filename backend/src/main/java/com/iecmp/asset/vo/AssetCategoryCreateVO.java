package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增资产分类 响应体。
 *
 * <p>对应接口：4-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetCategoryCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增分类 ID */
    @Schema(description = "新增分类 ID")
    private Long categoryId;
}
