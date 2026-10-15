package com.iecmp.repo.model;

import lombok.Data;

/**
 * 成员维度的贡献汇总行，接口 2-14（成员贡献排行）与 2-12（个人排名）共用。
 *
 * <p>由 {@code RepoCommitMapper.xml} 按 {@code user_id} 聚合 {@code repo_commit} 得到，
 * 再左连 {@code sys_user} 取姓名 / 年级 / 头像。
 */
@Data
public class RepoUserSummary {

    /** 成员用户 ID。 */
    private Long userId;

    /** 成员姓名，来自 {@code sys_user.real_name}。 */
    private String realName;

    /** 年级，来自 {@code sys_user.grade}。 */
    private String grade;

    /** 头像地址，来自 {@code sys_user.avatar_url}。 */
    private String avatarUrl;

    /** 提交总数（已排除合并提交）。 */
    private Integer commitCount;

    /** 活跃天数：有提交的自然日数量。 */
    private Integer activeDays;

    /** 新增代码行数。 */
    private Integer addLines;

    /** 删除代码行数。 */
    private Integer delLines;

    /** 有提交的仓库数，仅接口 2-12 使用（{@code projectCount}）。 */
    private Integer projectCount;

    /** 有提交的分支数，仅接口 2-12 使用（{@code branchCount}）。 */
    private Integer branchCount;
}
