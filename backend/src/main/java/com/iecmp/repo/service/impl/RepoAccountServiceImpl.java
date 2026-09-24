package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.repo.entity.RepoAccount;
import com.iecmp.repo.mapper.RepoAccountMapper;
import com.iecmp.repo.service.RepoAccountService;
import org.springframework.stereotype.Service;

/**
 * 托管账户业务实现（{@code repo_account}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link RepoAccountService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class RepoAccountServiceImpl extends ServiceImpl<RepoAccountMapper, RepoAccount>
        implements RepoAccountService {
}
