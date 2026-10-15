package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 调换申请审核（负责人） 请求体。
 *
 * <p>对应接口：5-24（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatSwapAuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 通过，2 驳回（与全平台审核接口一致，见 3.2 节） */
    @Schema(description = "1 通过，2 驳回（与全平台审核接口一致，见 3.2 节）")
    @NotNull(message = "1 通过，2 驳回（与全平台审核接口一致，见 3.2 节）不能为空")
    private Integer auditStatus;

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;
}
