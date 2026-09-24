package com.iecmp.common.context;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 当前登录用户（请求级上下文对象）。
 *
 * <p>由 {@code AuthInterceptor} 解析令牌后写入 {@link UserContext}，
 * 业务代码通过 {@code LoginUserUtil} 或 {@link UserContext} 获取，不直接从请求参数取 userId。
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID。 */
    private Long userId;

    /** 登录账号。 */
    private String username;

    /** 姓名。 */
    private String realName;

    /** 角色编码集合，如 CENTER_HEAD、MEMBER。 */
    private Set<String> roles = new HashSet<>();

    /** 权限码集合，超级管理员为空集合并由 superAdmin 标记短路放行。 */
    private Set<String> permissions = new HashSet<>();

    /** 是否超级管理员：为 true 时跳过权限码校验。 */
    private boolean superAdmin;
}
