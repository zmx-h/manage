package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.repo.entity.RepoProject;
import com.iecmp.repo.mapper.RepoProjectMapper;
import com.iecmp.repo.service.RepoProjectService;
import org.springframework.stereotype.Service;

/**
 * 仓库业务实现（{@code repo_project}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link RepoProjectService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class RepoProjectServiceImpl extends ServiceImpl<RepoProjectMapper, RepoProject>
        implements RepoProjectService {
}
