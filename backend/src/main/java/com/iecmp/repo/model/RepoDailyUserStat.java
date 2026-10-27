package com.iecmp.repo.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * 「日期 × 成员」粒度的提交聚合行，由 {@code RepoCommitMapper.xml} 直接映射。
 *
 * <p>接口 2-10 / 2-11 / 2-15 都由这一份数据派生：
 * <ul>
 *   <li>按日汇总 → 趋势图的提交数与增删行数；</li>
 *   <li>按周期分桶 → 周 / 月粒度的折线；</li>
 *   <li>周期内 {@code userId} 去重计数 → 接口 2-15 的 {@code memberCount}</li>
 *   （去重必须在分桶后做，不能把「每日活跃人数」相加，否则同一个人会被重复计数）。</li>
 * </ul>
 */
@Data
public class RepoDailyUserStat {

    /** 提交日期（已按 {@code DATE(commit_time)} 截断）。 */
    private LocalDate statDate;

    /** 成员用户 ID。 */
    private Long userId;

    /** 当日提交数。 */
    private Integer commitCount;

    /** 当日新增行数。 */
    private Integer addLines;

    /** 当日删除行数。 */
    private Integer delLines;
}
