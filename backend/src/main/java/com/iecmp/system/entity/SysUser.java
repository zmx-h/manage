package com.iecmp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户表：表 {@code sys_user}。
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
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 登录账号，4~32 位。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("username")
    private String username;

    /**
     * 密码，BCrypt 加盐哈希。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("password")
    @JsonIgnore
    private String password;

    /**
     * 姓名。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("real_name")
    private String realName;

    /**
     * 学号，全局唯一。
     */
    @TableField("student_no")
    private String studentNo;

    /**
     * 年级，如 2023。
     */
    @TableField("grade")
    private String grade;

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
     * 邮箱。
     */
    @TableField("email")
    private String email;

    /**
     * 头像地址（OSS）。
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 状态：0 禁用 1 启用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 最近登录时间。
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
