package com.iecmp.repo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 托管账户表：表 {@code repo_account}。
 *
 * <p>所属：模块二 · 代码仓库关联。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author WanZiyu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("repo_account")
public class RepoAccount extends BaseEntity {

    /**
     * 所属用户 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 平台：gitee / github。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("platform")
    private String platform;

    /**
     * 托管平台账号名。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("account_name")
    private String accountName;

    /**
     * 个人访问令牌，AES 加密存储。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("access_token")
    @JsonIgnore
    private String accessToken;

    /**
     * 平台头像地址（直接用平台外链，不转存 OSS）。
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 状态：0 失效 1 有效。
     */
    @TableField("status")
    private Integer status;

    /**
     * 采集状态：0 未采集 1 采集中 2 采集正常 3 采集失败。
     */
    @TableField("sync_status")
    private Integer syncStatus;

    /**
     * 最近采集完成时间。
     */
    @TableField("last_sync_time")
    private LocalDateTime lastSyncTime;

    /**
     * 绑定时间。
     */
    @TableField("bind_time")
    private LocalDateTime bindTime;
}
