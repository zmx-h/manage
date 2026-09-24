package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 注册申请审核 请求体。
 *
 * <p>对应接口：1-26（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RegisterApplyAuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 通过，2 驳回 */
    @Schema(description = "1 通过，2 驳回")
    @NotNull(message = "1 通过，2 驳回不能为空")
    private Integer auditStatus;

    /** 通过时生效，分配的角色 ID 列表 */
    @Schema(description = "通过时生效，分配的角色 ID 列表")
    private List<Long> roleIds;

    /** 通过时可自定义登录账号 */
    @Schema(description = "通过时可自定义登录账号")
    private String username;

    /** 审核意见，驳回时建议填写 */
    @Schema(description = "审核意见，驳回时建议填写")
    private String auditRemark;
}
