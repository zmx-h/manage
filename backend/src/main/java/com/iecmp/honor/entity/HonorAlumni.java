package com.iecmp.honor.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学长学姐信息表：表 {@code honor_alumni}。
 *
 * <p>所属：模块六 · 学长学姐展板。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("honor_alumni")
public class HonorAlumni extends BaseEntity {

    /**
     * 姓名。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("name")
    private String name;

    /**
     * 性别：0 未知 1 男 2 女。
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 毕业年份，如 2024。
     */
    @TableField("graduation_year")
    private String graduationYear;

    /**
     * 专业。
     */
    @TableField("major")
    private String major;

    /**
     * 班级。
     */
    @TableField("class_name")
    private String className;

    /**
     * 就职单位。
     */
    @TableField("company")
    private String company;

    /**
     * 职位。
     */
    @TableField("position")
    private String position;

    /**
     * 头像地址（OSS）。
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 个人简介。
     */
    @TableField("brief")
    private String brief;

    /**
     * 标签，逗号分隔。
     */
    @TableField("tags")
    private String tags;

    /**
     * 填写人 ID（成员自填）。
     */
    @TableField("submit_user_id")
    private Long submitUserId;

    /**
     * 审核状态：0 待审核 1 已通过 2 已驳回。
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
     * 上板状态：0 下板 1 上板。
     */
    @TableField("is_show")
    private Integer isShow;

    /**
     * 展示排序，升序。
     */
    @TableField("sort_no")
    private Integer sortNo;
}
