package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 发起盘点 请求体。
 *
 * <p>对应接口：4-23（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetInventoryAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 盘点任务标题，最长 100 字 */
    @Schema(description = "盘点任务标题，最长 100 字")
    @NotBlank(message = "盘点任务标题，最长 100 字不能为空")
    private String title;

    /** 限定盘点某分类下的资产；不传表示全部资产 */
    @Schema(description = "限定盘点某分类下的资产；不传表示全部资产")
    private Long categoryId;

    /** 是否排除已报废资产：0 不排除 1 排除 */
    @Schema(description = "是否排除已报废资产：0 不排除 1 排除")
    private Integer excludeScrapped;
}
