package com.iecmp.honor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 审核通过 / 驳回 请求体。
 *
 * <p>对应接口：6-18（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniAuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 通过，2 驳回 */
    @Schema(description = "1 通过，2 驳回")
    @NotNull(message = "1 通过，2 驳回不能为空")
    private Integer auditStatus;

    /** 通过时是否同时上板：0 不上板 1 上板 */
    @Schema(description = "通过时是否同时上板：0 不上板 1 上板")
    private Integer isShow;

    /** 审核意见，驳回时建议填写 */
    @Schema(description = "审核意见，驳回时建议填写")
    private String auditRemark;
}
