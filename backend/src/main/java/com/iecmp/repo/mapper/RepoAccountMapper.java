package com.iecmp.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.repo.entity.RepoAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 托管账户表（{@code repo_account}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/repo/RepoAccountMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_user_platform_account (user_id, platform, account_name, deleted)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface RepoAccountMapper extends BaseMapper<RepoAccount> {
}
