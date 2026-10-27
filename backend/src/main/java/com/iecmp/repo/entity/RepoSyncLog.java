package com.iecmp.repo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 同步日志表：表 {@code repo_sync_log}。
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
@TableName("repo_sync_log")
public class RepoSyncLog extends BaseEntity {

    /**
     * 托管账户 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 所属用户 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 采集类型：1 定时自动 2 管理员手动补采。
     */
    @TableField("sync_type")
    private Integer syncType;

    /**
     * 采集结果：0 失败 1 成功。
     */
    @TableField("sync_result")
    private Integer syncResult;

    /**
     * 本次采集到的提交数。
     */
    @TableField("commit_count")
    private Integer commitCount;

    /**
     * 耗时（毫秒）。
     */
    @TableField("cost_ms")
    private Long costMs;

    /**
     * 错误信息。
     */
    @TableField("error_msg")
    private String errorMsg;
}
