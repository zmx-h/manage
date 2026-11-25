package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/** 招新报名详情 响应体（接口 1-33）。 */
@Data
public class RecruitApplyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 报名记录 ID */
    @Schema(description = "报名记录 ID")
    private Long applyId;

    /** 学号 */
    @Schema(description = "学号")
    private String studentNo;

    /** 姓名 */
    @Schema(description = "姓名")
    private String realName;

    /** 年级 */
    @Schema(description = "年级")
    private String grade;

    /** 班级 */
    @Schema(description = "班级")
    private String className;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 个人简介 */
    @Schema(description = "个人简介")
    private String intro;

    /** 报名状态 */
    @Schema(description = "报名状态")
    private Integer status;

    /** 考核成绩 */
    @Schema(description = "考核成绩")
    private BigDecimal score;

    /** 成绩备注 */
    @Schema(description = "成绩备注")
    private String assessmentRemark;

    /** 成绩录入管理员 ID */
    @Schema(description = "成绩录入管理员 ID")
    private Long assessmentUserId;

    /** 成绩录入时间 */
    @Schema(description = "成绩录入时间")
    private String assessmentTime;

    /** 注册码生成时间 */
    @Schema(description = "注册码生成时间")
    private String codeCreateTime;

    /** 最近发信时间 */
    @Schema(description = "最近发信时间")
    private String codeSentTime;

    /** 注册码过期时间 */
    @Schema(description = "注册码过期时间")
    private String codeExpireTime;

    /** 累计发信次数 */
    @Schema(description = "累计发信次数")
    private Integer codeSendCount;

    /** 连续输错次数 */
    @Schema(description = "连续输错次数")
    private Integer codeAttempts;

    /** 注册码是否已过期 */
    @Schema(description = "注册码是否已过期")
    private Boolean codeExpired;

    /** 注册成功后生成的用户 ID */
    @Schema(description = "注册成功后生成的用户 ID")
    private Long createUserId;

    /** 报名来源 IP */
    @Schema(description = "报名来源 IP")
    private String ip;

    /** 报名时间 */
    @Schema(description = "报名时间")
    private String applyTime;
}
