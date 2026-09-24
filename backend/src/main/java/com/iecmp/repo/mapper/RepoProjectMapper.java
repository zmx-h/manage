package com.iecmp.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.repo.entity.RepoProject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库表（{@code repo_project}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/repo/RepoProjectMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_account_repo (account_id, repo_url, deleted)}</li>
 *   <li>{@code KEY idx_user_id (user_id)}</li>
 *   <li>{@code KEY idx_tracked (tracked)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface RepoProjectMapper extends BaseMapper<RepoProject> {
}
