package com.iecmp.repo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提交记录表：表 {@code repo_commit}。
 *
 * <p>所属：模块二 · 代码仓库关联。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("repo_commit")
public class RepoCommit extends BaseEntity {

    /**
     * 所属仓库 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 所属用户 ID（冗余，便于按用户统计）。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 提交 SHA。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("commit_sha")
    private String commitSha;

    /**
     * 所属分支（统计范围为所有分支）。
     */
    @TableField("branch")
    private String branch;

    /**
     * 提交作者名。
     */
    @TableField("author_name")
    private String authorName;

    /**
     * 提交作者邮箱。
     */
    @TableField("author_email")
    private String authorEmail;

    /**
     * 提交信息。
     */
    @TableField("message")
    private String message;

    /**
     * 提交时间。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("commit_time")
    private LocalDateTime commitTime;

    /**
     * 新增行数。
     */
    @TableField("add_lines")
    private Integer addLines;

    /**
     * 删除行数。
     */
    @TableField("del_lines")
    private Integer delLines;

    /**
     * 是否合并提交：0 否 1 是。
     */
    @TableField("is_merge")
    private Integer isMerge;
}
