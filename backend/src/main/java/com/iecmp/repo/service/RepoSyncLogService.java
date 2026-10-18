package com.iecmp.repo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.repo.entity.RepoSyncLog;
import com.iecmp.repo.query.RepoSyncLogPageQuery;
import com.iecmp.repo.vo.RepoSyncLogPageVO;

/**
 * 同步日志业务接口（{@code repo_sync_log}），覆盖接口 2-16。
 *
 * <p>日志由 {@code RepoSyncService} 在每次采集结束时写入（成功、失败都写），
 * 成员端不直接读本表，改由接口 2-06 汇总最近一次的采集结果。
 */
public interface RepoSyncLogService extends IService<RepoSyncLog> {

    /** 2-16 同步日志分页（后台）。 */
    PageResult<RepoSyncLogPageVO> pageLogs(RepoSyncLogPageQuery query);
}
