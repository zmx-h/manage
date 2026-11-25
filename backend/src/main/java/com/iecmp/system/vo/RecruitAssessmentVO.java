package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/** 招新考核成绩录入 响应体（接口 1-34）。 */
@Data
public class RecruitAssessmentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 报名记录 ID */
    @Schema(description = "报名记录 ID")
    private Long applyId;

    /** 考核成绩 */
    @Schema(description = "考核成绩")
    private BigDecimal score;

    /** 通过分 */
    @Schema(description = "通过分")
    private Integer passScore;

    /** 是否通过 */
    @Schema(description = "是否通过")
    private Boolean passed;

    /** 最新报名状态 */
    @Schema(description = "最新报名状态")
    private Integer status;

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
