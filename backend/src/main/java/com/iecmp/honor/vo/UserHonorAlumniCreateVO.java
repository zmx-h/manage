package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 成员提交学长信息 响应体。
 *
 * <p>对应接口：6-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserHonorAlumniCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提交记录 ID */
    @Schema(description = "提交记录 ID")
    private Long alumniId;

    /** 审核状态，固定返回 0 待审核 */
    @Schema(description = "审核状态，固定返回 0 待审核")
    private Integer status;
}
