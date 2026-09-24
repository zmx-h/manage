package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.repo.entity.RepoCommit;
import com.iecmp.repo.mapper.RepoCommitMapper;
import com.iecmp.repo.service.RepoCommitService;
import org.springframework.stereotype.Service;

/**
 * 提交记录业务实现（{@code repo_commit}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link RepoCommitService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class RepoCommitServiceImpl extends ServiceImpl<RepoCommitMapper, RepoCommit>
        implements RepoCommitService {
}
