package com.iecmp.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.repo.entity.RepoSyncLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 同步日志表（{@code repo_sync_log}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/repo/RepoSyncLogMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_account_create (account_id, create_time)}</li>
 *   <li>{@code KEY idx_user_id (user_id)}</li>
 *   <li>{@code KEY idx_sync_result (sync_result)}</li>
 *   <li>{@code KEY idx_create_time (create_time)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface RepoSyncLogMapper extends BaseMapper<RepoSyncLog> {
}
