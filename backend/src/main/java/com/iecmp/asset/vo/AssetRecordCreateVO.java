package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 变动记录登记 响应体。
 *
 * <p>对应接口：4-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetRecordCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 流水记录 ID */
    @Schema(description = "流水记录 ID")
    private Long recordId;
}
