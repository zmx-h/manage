package com.iecmp.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.repo.entity.RepoProject;
import com.iecmp.repo.model.RepoIdCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 仓库表（{@code repo_project}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询写在
 * {@code resources/mapper/repo/RepoProjectMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_account_repo (account_id, repo_url, deleted)}</li>
 *   <li>{@code KEY idx_user_create_time (user_id, create_time)}</li>
 *   <li>{@code KEY idx_tracked (tracked)}</li>
 * </ul>
 *
 * @author WanZiyu
 */
@Mapper
public interface RepoProjectMapper extends BaseMapper<RepoProject> {

    /**
     * 批量统计若干仓库已采集的提交数，供接口 2-07 的 {@code commitCount}。
     *
     * <p>接口文档 8.3.1 明确要求「列表查询时统一批量聚合，避免逐行查询」，
     * 因此这里一次分组查出当前页全部仓库的计数，由调用方在内存里回填。
     *
     * @param projectIds 仓库 ID 集合，调用方需保证非空
     */
    List<RepoIdCount> selectCommitCounts(@Param("projectIds") Collection<Long> projectIds);

    /**
     * 批量统计若干账户已采集的仓库数，供接口 2-03 的 {@code projectCount}。
     *
     * <p>与 {@link #selectCommitCounts} 同样的思路：一次分组查出全部账户的仓库数，
     * 而不是在循环里逐个 {@code selectCount}。
     *
     * @param accountIds 账户 ID 集合，调用方需保证非空
     */
    List<RepoIdCount> selectProjectCountsByAccount(@Param("accountIds") Collection<Long> accountIds);
}
