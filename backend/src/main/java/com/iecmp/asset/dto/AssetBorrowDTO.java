package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 借用 / 领用申请 请求体。
 *
 * <p>对应接口：4-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetBorrowDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资产 ID */
    @Schema(description = "资产 ID")
    @NotNull(message = "资产 ID不能为空")
    private Long assetId;

    /** 1 借用（需归还），2 领用（不归还） */
    @Schema(description = "1 借用（需归还），2 领用（不归还）")
    @NotNull(message = "1 借用（需归还），2 领用（不归还）不能为空")
    private Integer borrowType;

    /** 用途说明，最长 255 字 */
    @Schema(description = "用途说明，最长 255 字")
    private String purpose;

    /** 预计归还时间，格式 yyyy-MM-dd HH:mm:ss；借用时建议填写 */
    @Schema(description = "预计归还时间，格式 yyyy-MM-dd HH:mm:ss；借用时建议填写")
    private String expectReturnTime;
}
