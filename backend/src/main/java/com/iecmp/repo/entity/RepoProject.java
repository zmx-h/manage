package com.iecmp.repo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库表：表 {@code repo_project}。
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
@TableName("repo_project")
public class RepoProject extends BaseEntity {

    /**
     * 所属托管账户 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 所属用户 ID（冗余，便于按用户统计）。
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
     * 仓库名称。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("repo_name")
    private String repoName;

    /**
     * 仓库地址。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("repo_url")
    private String repoUrl;

    /**
     * 仓库所有者。
     */
    @TableField("owner_name")
    private String ownerName;

    /**
     * 主要语言。
     */
    @TableField("language")
    private String language;

    /**
     * 默认分支。
     */
    @TableField("default_branch")
    private String defaultBranch;

    /**
     * Star 数。
     */
    @TableField("star_count")
    private Integer starCount;

    /**
     * Fork 数。
     */
    @TableField("fork_count")
    private Integer forkCount;

    /**
     * 最近推送时间。
     */
    @TableField("last_push_time")
    private LocalDateTime lastPushTime;

    /**
     * 是否纳入统计：0 不统计 1 统计。
     */
    @TableField("tracked")
    private Integer tracked;

    /**
     * 采集状态：0 未采集 1 采集中 2 采集正常 3 采集失败。
     */
    @TableField("sync_status")
    private Integer syncStatus;
}
