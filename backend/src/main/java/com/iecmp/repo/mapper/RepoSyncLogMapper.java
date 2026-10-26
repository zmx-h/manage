package com.iecmp.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iecmp.repo.entity.RepoSyncLog;
import com.iecmp.repo.query.RepoSyncLogPageQuery;
import com.iecmp.repo.vo.RepoSyncLogPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 同步日志表（{@code repo_sync_log}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；接口 2-16 需要展示账户名与成员姓名，
 * 属多表查询，写在 {@code resources/mapper/repo/RepoSyncLogMapper.xml} 中。
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
 * @author WanZiyu
 */
@Mapper
public interface RepoSyncLogMapper extends BaseMapper<RepoSyncLog> {

    /**
     * 后台同步日志分页（接口 2-16）：日志左连账户表与用户表，补齐平台、账号名与成员姓名。
     *
     * @param orderByClause 已由 {@code RepoSortUtils} 白名单校验过的排序片段，为 null 时用默认排序。
     *                      排序字段名无法用 {@code #{}} 占位，只能 {@code ${}} 拼接，
     *                      因此调用方必须保证该值只可能来自白名单。
     */
    IPage<RepoSyncLogPageVO> selectLogPage(IPage<RepoSyncLogPageVO> page,
                                           @Param("q") RepoSyncLogPageQuery query,
                                           @Param("orderByClause") String orderByClause);

    /**
     * 批量取每个账户最近一条同步日志，供接口 2-06 展示「最近一次采集结果 / 新增提交数 / 失败原因」。
     *
     * <p>用 {@code idx_account_create} 走「每账户取最新一条」，避免把全部日志拉回内存再分组。
     *
     * @param accountIds 账户 ID 集合，调用方需保证非空
     */
    List<RepoSyncLog> selectLatestByAccountIds(@Param("accountIds") Collection<Long> accountIds);
}
