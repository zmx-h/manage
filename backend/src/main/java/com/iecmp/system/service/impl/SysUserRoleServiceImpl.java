package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.system.entity.SysUserRole;
import com.iecmp.system.mapper.SysUserRoleMapper;
import com.iecmp.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联业务实现（{@code sys_user_role}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysUserRoleService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleService {
}
