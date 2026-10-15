package com.iecmp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 注册申请表：表 {@code sys_register_apply}。
 *
 * <p>所属：模块一 · 用户与权限。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_register_apply")
public class SysRegisterApply extends BaseEntity {

    /**
     * 学号，全局唯一。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("student_no")
    private String studentNo;

    /**
     * 真实姓名。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("real_name")
    private String realName;

    /**
     * 专业。
     */
    @TableField("major")
    private String major;

    /**
     * 手机号。
     */
    @TableField("phone")
    private String phone;

    /**
     * 申请理由，最长 200 字。
     */
    @TableField("reason")
    private String reason;

    /**
     * 状态：0 待审核 1 已通过 2 已驳回。
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核人 ID。
     */
    @TableField("audit_user_id")
    private Long auditUserId;

    /**
     * 审核时间。
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核意见。
     */
    @TableField("audit_remark")
    private String auditRemark;

    /**
     * 通过后生成的用户 ID。
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 提交来源 IP。
     */
    @TableField("ip")
    private String ip;

    /**
     * 申请时间。
     */
    @TableField("apply_time")
    private LocalDateTime applyTime;
}
