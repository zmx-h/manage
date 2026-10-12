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

    /**
     * 获取当前登录用户 ID，未登录时返回 {@code 0L}（表示系统操作）。
     *
     * <p>用于写入 NOT NULL 字段（如 {@code asset_record.operator_id}、
     * {@code asset_borrow.user_id}）时兜底，避免白名单跳过登录导致整条 INSERT 失败。
     */
    public static Long getUserIdOrDefault() {
        Long userId = getUserId();
        return userId != null ? userId : 0L;
    }

    /** 请求结束时清理，避免线程复用造成串号。 */
    public static void clear() {
        HOLDER.remove();
    }
}
