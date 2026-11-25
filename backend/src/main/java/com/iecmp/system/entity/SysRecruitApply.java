package com.iecmp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招新报名表：表 {@code sys_recruit_apply}。
 *
 * <p>外部招新网页提交报名，管理员录入一轮考核成绩；通过后生成一次性邮箱注册码。
 * 注册码只保存 HMAC-SHA256 摘要，明文仅在发信或管理员补发时短暂出现。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_recruit_apply")
public class SysRecruitApply extends BaseEntity {

    /** 学号，可选。 */
    @TableField("student_no")
    private String studentNo;

    /** 姓名。 */
    @TableField("real_name")
    private String realName;

    /** 年级。 */
    @TableField("grade")
    private String grade;

    /** 班级。 */
    @TableField("class_name")
    private String className;

    /** 邮箱，统一小写存储。 */
    @TableField("email")
    private String email;

    /** 手机号。 */
    @TableField("phone")
    private String phone;

    /** 个人简介。 */
    @TableField("intro")
    private String intro;

    /** 状态：0 待考核 1 已通过待发码 2 未通过 3 已发码 4 已注册 5 注册码已过期。 */
    @TableField("status")
    private Integer status;

    /** 考核成绩。 */
    @TableField("score")
    private java.math.BigDecimal score;

    /** 成绩备注。 */
    @TableField("assessment_remark")
    private String assessmentRemark;

    /** 录入成绩的管理员 ID。 */
    @TableField("assessment_user_id")
    private Long assessmentUserId;

    /** 录入成绩时间。 */
    @TableField("assessment_time")
    private LocalDateTime assessmentTime;

    /** 注册码 HMAC-SHA256(code + pepper)。 */
    @TableField("code_hash")
    private String codeHash;

    /** 注册码过期时间。 */
    @TableField("code_expire_time")
    private LocalDateTime codeExpireTime;

    /** 注册码生成时间。 */
    @TableField("code_create_time")
    private LocalDateTime codeCreateTime;

    /** 最近发信时间。 */
    @TableField("code_sent_time")
    private LocalDateTime codeSentTime;

    /** 累计发信次数。 */
    @TableField("code_send_count")
    private Integer codeSendCount;

    /** 连续输错次数。 */
    @TableField("code_attempts")
    private Integer codeAttempts;

    /** 注册成功后生成的用户 ID。 */
    @TableField("create_user_id")
    private Long createUserId;

    /** 报名来源 IP。 */
    @TableField("ip")
    private String ip;

    /** 报名时间。 */
    @TableField("apply_time")
    private LocalDateTime applyTime;
}
