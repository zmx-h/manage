package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 资产状态变更 请求体。
 *
 * <p>对应接口：4-13（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标状态：2 闲置，3 维修中，5 已报废 */
    @Schema(description = "目标状态：2 闲置，3 维修中，5 已报废")
    @NotNull(message = "目标状态：2 闲置，3 维修中，5 已报废不能为空")
    private Integer status;

    /** 变更原因，如「风扇异响送修」 */
    @Schema(description = "变更原因，如「风扇异响送修」")
    private String remark;
}
