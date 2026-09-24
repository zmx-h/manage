package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 结束盘点 请求体。
 *
 * <p>对应接口：4-26（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetInventoryFinishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 「盘亏」明细是否同步把资产状态改为「已报废」：0 否 1 是 */
    @Schema(description = "「盘亏」明细是否同步把资产状态改为「已报废」：0 否 1 是")
    private Integer updateAssetStatus;
}
