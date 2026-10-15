package com.iecmp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表：表 {@code sys_role}。
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
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色名称，最长 32 字。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色编码：SUPER_ADMIN / CENTER_HEAD / MINISTER / MEMBER。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 数据范围：1 全部数据 2 本人数据。
     */
    @TableField("data_scope")
    private Integer dataScope;

    /**
     * 排序号，升序。
     */
    @TableField("sort_no")
    private Integer sortNo;

    /**
     * 状态：0 停用 1 启用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
