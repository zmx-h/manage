package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 工位申请审核 请求体。
 *
 * <p>对应接口：5-19（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatApplyAuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 通过，2 驳回 */
    @Schema(description = "1 通过，2 驳回")
    @NotNull(message = "1 通过，2 驳回不能为空")
    private Integer auditStatus;

    /** 通过时实际分配的工位 ID；申请仅填了意向区域时可在此指定 */
    @Schema(description = "通过时实际分配的工位 ID；申请仅填了意向区域时可在此指定")
    private Long assignSeatId;

    /** 审核意见，驳回时建议填写 */
    @Schema(description = "审核意见，驳回时建议填写")
    private String auditRemark;
}
