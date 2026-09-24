package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.repo.entity.RepoSyncLog;
import com.iecmp.repo.mapper.RepoSyncLogMapper;
import com.iecmp.repo.service.RepoSyncLogService;
import org.springframework.stereotype.Service;

/**
 * 同步日志业务实现（{@code repo_sync_log}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link RepoSyncLogService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class RepoSyncLogServiceImpl extends ServiceImpl<RepoSyncLogMapper, RepoSyncLog>
        implements RepoSyncLogService {
}
