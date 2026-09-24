package com.iecmp.repo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.repo.entity.RepoSyncLog;

/**
 * 同步日志业务接口（{@code repo_sync_log}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author IECMP 后端组
 */
public interface RepoSyncLogService extends IService<RepoSyncLog> {
}
