package com.iecmp.common.util;

import com.iecmp.common.context.LoginUser;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.ResultCode;

/**
 * 当前登录用户读取工具：业务代码取 userId 一律走这里，不要从请求参数传入。
 */
public final class LoginUserUtil {

    private LoginUserUtil() {
    }

    /** 取当前登录用户，未登录抛 401。 */
    public static LoginUser getLoginUser() {
        LoginUser user = UserContext.get();
        if (user == null || user.getUserId() == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return user;
    }

    /** 取当前登录用户 ID。 */
    public static Long getUserId() {
        return getLoginUser().getUserId();
    }

    /** 取当前登录用户 ID，未登录返回 null（用于「令牌可选」的公开接口）。 */
    public static Long getUserIdOrNull() {
        LoginUser user = UserContext.get();
        return user == null ? null : user.getUserId();
    }

    /** 是否超级管理员。 */
    public static boolean isSuperAdmin() {
        LoginUser user = UserContext.get();
        return user != null && user.isSuperAdmin();
    }
}
