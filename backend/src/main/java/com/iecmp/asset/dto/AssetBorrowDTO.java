package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "借用类型不能为空")
    @Min(value = 1, message = "借用类型只能为 1 借用或 2 领用")
    @Max(value = 2, message = "借用类型只能为 1 借用或 2 领用")
    private Integer borrowType;

    /** 用途说明，最长 255 字 */
    @Schema(description = "用途说明，最长 255 字")
    @Size(max = 255, message = "用途说明长度不能超过 255 个字符")
    private String purpose;

    /** 预计归还时间，格式 yyyy-MM-dd HH:mm:ss；借用时建议填写 */
    @Schema(description = "预计归还时间，格式 yyyy-MM-dd HH:mm:ss；借用时建议填写")
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})?$",
            message = "预计归还时间格式应为 yyyy-MM-dd HH:mm:ss")
    private String expectReturnTime;
}
