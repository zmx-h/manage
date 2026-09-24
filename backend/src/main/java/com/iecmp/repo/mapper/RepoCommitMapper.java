package com.iecmp.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.repo.entity.RepoCommit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提交记录表（{@code repo_commit}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/repo/RepoCommitMapper.xml} 中，方法在此声明。
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
 * @author IECMP 后端组
 */
@Mapper
public interface RepoCommitMapper extends BaseMapper<RepoCommit> {
}
