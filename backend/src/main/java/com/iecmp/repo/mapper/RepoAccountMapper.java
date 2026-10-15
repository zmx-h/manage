package com.iecmp.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iecmp.repo.entity.RepoAccount;
import com.iecmp.repo.query.RepoAccountPageQuery;
import com.iecmp.repo.vo.RepoAccountPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 托管账户表（{@code repo_account}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；接口 2-13 需要关联 {@code sys_user} 取姓名 / 年级，
 * 属多表查询，写在 {@code resources/mapper/repo/RepoAccountMapper.xml} 中。
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

    /**
     * 后台账户分页（接口 2-13）：账户左连 {@code sys_user}，并带上仓库数与提交数。
     *
     * <p>仓库数 / 提交数用相关子查询取——分页后每页至多 100 行，比先查账户再回表聚合更省一次往返，
     * 也避免「按提交数排序却要先聚合全表」的问题。
     *
     * @param orderByClause 已由 {@code RepoSortUtils} 白名单校验过的排序片段，为 null 时用默认排序。
     *                      排序字段名无法用 {@code #{}} 占位，只能 {@code ${}} 拼接，
     *                      因此调用方必须保证该值只可能来自白名单。
     */
    IPage<RepoAccountPageVO> selectAccountPage(IPage<RepoAccountPageVO> page,
                                               @Param("q") RepoAccountPageQuery query,
                                               @Param("orderByClause") String orderByClause);
}
