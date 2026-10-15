package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 发起盘点 响应体。
 *
 * <p>对应接口：4-23（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetInventoryCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 盘点任务 ID */
    @Schema(description = "盘点任务 ID")
    private Long inventoryId;

    /** 生成的待盘点明细数量 */
    @Schema(description = "生成的待盘点明细数量")
    private Integer totalCount;
}
