package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/** 招新注册码补发 响应体（接口 1-35）。 */
@Data
public class RecruitCodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 报名记录 ID */
    @Schema(description = "报名记录 ID")
    private Long applyId;

    /** 最新报名状态 */
    @Schema(description = "最新报名状态")
    private Integer status;

    /** 邮件发送失败时返回的注册码，成功时为 null */
    @Schema(description = "邮件发送失败时返回的注册码，成功时为 null")
    private String code;

    /** 注册码过期时间 */
    @Schema(description = "注册码过期时间")
    private String codeExpireTime;

    /** 邮件是否发送成功 */
    @Schema(description = "邮件是否发送成功")
    private Boolean mailSent;

    /** 邮件发送失败原因，成功时为 null */
    @Schema(description = "邮件发送失败原因，成功时为 null")
    private String mailError;
}
