package com.iecmp.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 借用审核 请求体。
 *
 * <p>对应接口：4-21（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetBorrowAuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 通过，2 驳回 */
    @Schema(description = "1 通过，2 驳回")
    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态只能为 1 通过或 2 驳回")
    @Max(value = 2, message = "审核状态只能为 1 通过或 2 驳回")
    private Integer auditStatus;

    /** 审核意见，驳回时建议填写 */
    @Schema(description = "审核意见，驳回时建议填写")
    @Size(max = 255, message = "审核意见长度不能超过 255 个字符")
    private String auditRemark;
}
