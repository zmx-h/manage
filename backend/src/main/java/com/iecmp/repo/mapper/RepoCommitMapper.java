package com.iecmp.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.repo.entity.RepoCommit;
import com.iecmp.repo.model.RepoDailyUserStat;
import com.iecmp.repo.model.RepoUserSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 提交记录表（{@code repo_commit}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；模块二全部统计口径都收敛在本接口的 XML 里
 * （{@code resources/mapper/repo/RepoCommitMapper.xml}），Controller / Service 不拼聚合 SQL。
 *
 * <p><b>统一的统计过滤条件</b>（三个接口共用，详见 XML 中的 {@code statFilters} 片段）：
 * <ol>
 *   <li>{@code is_merge = 0}：合并提交默认不计入统计（接口文档 8.1 已确定口径 6）；</li>
 *   <li>{@code repo_project.tracked = 1}：关闭统计的仓库不计入（接口文档 8.3.2）；</li>
 *   <li>提交所属仓库的账户 {@code status = 1}：令牌失效的账户不计入（接口文档 8.5.2）。</li>
 * </ol>
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_project_sha (project_id, commit_sha, deleted)}</li>
 *   <li>{@code KEY idx_user_commit_time (user_id, commit_time)}</li>
 *   <li>{@code KEY idx_project_commit_time (project_id, commit_time)}</li>
 *   <li>{@code KEY idx_commit_time (commit_time)}</li>
 * </ul>
 *
 * @author WanZiyu
 */
@Mapper
public interface RepoCommitMapper extends BaseMapper<RepoCommit> {

    /**
     * 批量插入提交记录，供采集任务使用。
     *
     * <p>首次全量采集一个仓库可能一次带回上千条提交，逐条 {@code insert} 会产生同样数量的往返，
     * 因此这里拼一条多值 INSERT。审计字段由 SQL 里的 {@code NOW()} 填充，
     * 不走 {@code MetaObjectHandler}——采集线程没有登录上下文，{@code create_by} 本就该为空。
     *
     * <p>去重依赖 {@code uk_project_sha (project_id, commit_sha, deleted)}：
     * 采集前已过滤掉库中存在的 SHA，并发场景下撞唯一键由调用方兜底。
     *
     * @param commits 待插入的提交，调用方需保证非空
     * @return 实际插入行数
     */
    int insertBatch(@Param("commits") List<RepoCommit> commits);

    /**
     * 按「日期 × 成员」聚合提交，供接口 2-10 / 2-11 / 2-15 派生趋势与热力图。
     *
     * @param userId            限定成员，为 null 表示全中心（接口 2-15）
     * @param projectId         限定仓库，为 null 表示全部仓库
     * @param begin             统计起始时间（含）
     * @param end               统计截止时间（含）
     * @param trackedOnly       是否只统计 {@code tracked = 1} 的仓库
     * @param activeAccountOnly 是否只统计 {@code repo_account.status = 1} 的账户
     */
    List<RepoDailyUserStat> selectDailyUserStats(@Param("userId") Long userId,
                                                 @Param("projectId") Long projectId,
                                                 @Param("begin") LocalDateTime begin,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("trackedOnly") boolean trackedOnly,
                                                 @Param("activeAccountOnly") boolean activeAccountOnly);

    /**
     * 单个成员在区间内的贡献汇总，供接口 2-12。
     *
     * <p><b>不返回</b> {@code realName / grade / avatarUrl}——这三个字段来自 {@code sys_user}，
     * 只有 {@link #selectUserSummaries} 会连表取。别拿本方法的返回值去填个人资料，
     * 取到的会是 null。
     *
     * @param trackedOnly       是否只统计 {@code tracked = 1} 的仓库
     * @param activeAccountOnly 是否只统计 {@code repo_account.status = 1} 的账户
     * @return 没有任何提交时返回 null（不返回全 0 的行）
     */
    RepoUserSummary selectUserSummary(@Param("userId") Long userId,
                                      @Param("begin") LocalDateTime begin,
                                      @Param("end") LocalDateTime end,
                                      @Param("trackedOnly") boolean trackedOnly,
                                      @Param("activeAccountOnly") boolean activeAccountOnly);

    /**
     * 全中心成员的贡献汇总，供接口 2-14。
     *
     * <p>排序在 SQL 内完成：{@code sortField} 与三项权重都以参数传入，
     * XML 里用 {@code <choose>} 选择排序列，不做字符串拼接，避免注入。
     *
     * @param sortField         1 提交数 2 活跃天数 3 代码行数 4 贡献度得分
     * @param trackedOnly       是否只统计 {@code tracked = 1} 的仓库
     * @param activeAccountOnly 是否只统计 {@code repo_account.status = 1} 的账户
     */
    List<RepoUserSummary> selectUserSummaries(@Param("begin") LocalDateTime begin,
                                              @Param("end") LocalDateTime end,
                                              @Param("sortField") Integer sortField,
                                              @Param("commitWeight") BigDecimal commitWeight,
                                              @Param("activeDayWeight") BigDecimal activeDayWeight,
                                              @Param("codeLineWeight") BigDecimal codeLineWeight,
                                              @Param("limit") Integer limit,
                                              @Param("trackedOnly") boolean trackedOnly,
                                              @Param("activeAccountOnly") boolean activeAccountOnly);

    /**
     * 统计提交数严格大于给定值的成员数，用于接口 2-12 的 {@code rank}（名次 = 该值 + 1）。
     *
     * <p>口径必须与 {@link #selectUserSummaries} 完全一致，否则个人排名会和排行榜对不上。
     */
    int countUsersWithMoreCommits(@Param("commitCount") Integer commitCount,
                                  @Param("begin") LocalDateTime begin,
                                  @Param("end") LocalDateTime end,
                                  @Param("trackedOnly") boolean trackedOnly,
                                  @Param("activeAccountOnly") boolean activeAccountOnly);
}
