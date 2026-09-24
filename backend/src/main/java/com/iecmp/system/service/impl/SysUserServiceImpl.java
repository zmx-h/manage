package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.mapper.SysUserMapper;
import com.iecmp.system.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户业务实现（{@code sys_user}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysUserService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {
}
