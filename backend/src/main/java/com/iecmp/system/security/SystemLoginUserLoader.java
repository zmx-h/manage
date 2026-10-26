package com.iecmp.system.security;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iecmp.common.context.LoginUser;
import com.iecmp.common.security.LoginUserLoader;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.entity.SysRolePermission;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.entity.SysUserRole;
import com.iecmp.system.enums.SystemErrorCode;
import com.iecmp.system.mapper.SysRoleMapper;
import com.iecmp.system.mapper.SysRolePermissionMapper;
import com.iecmp.system.mapper.SysUserMapper;
import com.iecmp.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 从 {@code sys_user / sys_user_role / sys_role / sys_role_permission}
 * 装配当前登录用户。
 */
@Component
@RequiredArgsConstructor
public class SystemLoginUserLoader implements LoginUserLoader {

    private static final String SUPER_ADMIN = "SUPER_ADMIN";

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public LoginUser load(Long userId) {
        if (userId == null) {
            return null;
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new com.iecmp.common.exception.BusinessException(
                    SystemErrorCode.E10002.getCode(), SystemErrorCode.E10002.getMsg());
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRealName(user.getRealName());
        loginUser.setRoles(new TreeSet<>());
        loginUser.setPermissions(new TreeSet<>());

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return loginUser;
        }

        Set<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .filter(roleId -> roleId != null)
                .collect(Collectors.toSet());
        if (roleIds.isEmpty()) {
            return loginUser;
        }

        List<SysRole> roles = sysRoleMapper.selectList(Wrappers.<SysRole>lambdaQuery()
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, 1));
        if (roles.isEmpty()) {
            return loginUser;
        }

        Set<Long> activeRoleIds = new TreeSet<>();
        for (SysRole role : roles) {
            if (role.getRoleCode() != null) {
                loginUser.getRoles().add(role.getRoleCode());
            }
            activeRoleIds.add(role.getId());
        }
        loginUser.setSuperAdmin(loginUser.getRoles().contains(SUPER_ADMIN));

        List<SysRolePermission> permissions = sysRolePermissionMapper.selectList(
                Wrappers.<SysRolePermission>lambdaQuery().in(SysRolePermission::getRoleId, activeRoleIds));
        for (SysRolePermission permission : permissions) {
            if (permission.getPermCode() != null && !permission.getPermCode().isBlank()) {
                loginUser.getPermissions().add(permission.getPermCode());
            }
        }
        return loginUser;
    }
}