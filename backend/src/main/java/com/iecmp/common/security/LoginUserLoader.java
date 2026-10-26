package com.iecmp.common.security;

import com.iecmp.common.context.LoginUser;

/**
 * 登录用户装配扩展点。
 *
 * <p>由 system 模块实现，避免 common 层反向依赖具体业务 Mapper。
 */
public interface LoginUserLoader {

    /**
     * 按用户 ID 装配角色、权限与状态。
     *
     * @param userId 用户 ID
     * @return 登录用户；用户不存在时返回 {@code null}
     */
    LoginUser load(Long userId);
}
