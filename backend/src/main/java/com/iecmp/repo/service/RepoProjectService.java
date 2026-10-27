package com.iecmp.repo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.repo.entity.RepoProject;
import com.iecmp.repo.query.RepoProjectPageQuery;
import com.iecmp.repo.vo.RepoProjectPageVO;

/**
 * 仓库业务接口（{@code repo_project}），覆盖接口 2-07、2-08。
 */
public interface RepoProjectService extends IService<RepoProject> {

    /**
     * 2-07 我的仓库分页。
     *
     * <p>{@code commitCount} 由 Mapper 一次分组查出当前页全部仓库的计数后回填，
     * 不在循环里逐行查询（接口文档 8.3.1 的备注）。
     */
    PageResult<RepoProjectPageVO> pageMyProjects(RepoProjectPageQuery query);

    /**
     * 2-08 开启 / 关闭仓库统计。
     *
     * <p>只改 {@code tracked} 标记，<b>不删除</b>已采集的提交记录——接口文档 8.3.2 明确
     * 「重新开启后历史提交立即恢复计入，无需重新同步」。
     *
     * @throws com.iecmp.common.exception.BusinessException 20004 仓库不存在；20005 仓库不在当前用户名下
     */
    void updateTracked(Long projectId, Integer tracked);
}
