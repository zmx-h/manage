package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.mapper.SysRoleMapper;
import com.iecmp.system.service.SysRoleService;
import org.springframework.stereotype.Service;

/**
 * 角色业务实现（{@code sys_role}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysRoleService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {
}
