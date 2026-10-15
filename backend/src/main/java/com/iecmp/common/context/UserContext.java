package com.iecmp.common.context;

/**
 * 当前登录用户的线程上下文。
 *
 * <p>由 {@code AuthInterceptor} 在请求进入时写入、请求结束时清理（finally 中调用 {@link #clear()}），
 * 业务代码在同步链路内可直接获取；异步线程中不可用，需显式传参。
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    /** 写入当前登录用户。 */
    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    /** 获取当前登录用户，未登录返回 null。 */
    public static LoginUser get() {
        return HOLDER.get();
    }

    /** 获取当前登录用户 ID，未登录返回 null。 */
    public static Long getUserId() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.getUserId();
    }

    /** 请求结束时清理，避免线程复用造成串号。 */
    public static void clear() {
        HOLDER.remove();
    }
}
