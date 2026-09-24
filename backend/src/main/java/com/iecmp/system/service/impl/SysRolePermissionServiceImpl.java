package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.system.entity.SysRolePermission;
import com.iecmp.system.mapper.SysRolePermissionMapper;
import com.iecmp.system.service.SysRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 角色权限关联业务实现（{@code sys_role_permission}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysRolePermissionService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission>
        implements SysRolePermissionService {
}
