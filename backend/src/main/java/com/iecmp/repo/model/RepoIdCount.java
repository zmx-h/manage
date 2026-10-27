package com.iecmp.repo.model;

import lombok.Data;

/**
 * 「主键 → 计数」的通用聚合行。
 *
 * <p>用于接口 2-07 的 {@code commitCount}：按 {@code project_id} 对 {@code repo_commit}
 * 一次分组统计出全部仓库的提交数，避免逐行查询（接口文档 8.3.1 的备注要求）。
 */
@Data
public class RepoIdCount {

    /** 被统计的主键，如 {@code repo_project.id}。 */
    private Long id;

    /** 计数结果。 */
    private Integer count;
}
