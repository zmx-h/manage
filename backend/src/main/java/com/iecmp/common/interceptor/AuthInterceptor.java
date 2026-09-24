package com.iecmp.common.interceptor;

import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.context.LoginUser;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.IecmpProperties;
import com.iecmp.common.properties.JwtProperties;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 鉴权拦截器（架构文档 4.1 / 4.5 节）。
 *
 * <p>处理顺序：
 * <ol>
 *   <li>命中 {@code iecmp.auth.ignore-urls} 的路径直接放行（公开接口）；</li>
 *   <li>读取 {@code Authorization: Bearer {token}}，解析令牌并写入 {@link UserContext}；</li>
 *   <li>命中 {@code iecmp.auth.optional-urls} 的路径解析失败也放行（令牌可选接口）；</li>
 *   <li>校验方法上的 {@link RequiresPerm} 权限码。</li>
 * </ol>
 *
 * <p>未登录统一返回 401，权限不足返回 403；Redis 故障时鉴权直接失败（不放行）。
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final TokenService tokenService;
    private final JwtProperties jwtProperties;
    private final IecmpProperties iecmpProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        // 1. 公开接口
        if (matches(iecmpProperties.getAuth().getIgnoreUrls(), path)) {
            return true;
        }

        String token = resolveToken(request);
        boolean optional = matches(iecmpProperties.getAuth().getOptionalUrls(), path);

        // 2. 解析令牌
        if (token == null || token.isEmpty()) {
            if (optional) {
                return true;
            }
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        LoginUser loginUser = tokenService.resolveAccessToken(token);
        UserContext.set(loginUser);

        // 3. 令牌可选接口到此为止
        if (optional) {
            return true;
        }

        // 4. 权限码校验
        if (handler instanceof HandlerMethod handlerMethod) {
            RequiresPerm requiresPerm = handlerMethod.getMethodAnnotation(RequiresPerm.class);
            if (requiresPerm != null && !hasPermission(loginUser, requiresPerm.value())) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean hasPermission(LoginUser loginUser, String[] required) {
        if (loginUser.isSuperAdmin()) {
            return true;
        }
        if (loginUser.getPermissions() == null || loginUser.getPermissions().isEmpty()) {
            return false;
        }
        for (String perm : required) {
            if (loginUser.getPermissions().contains(perm)) {
                return true;
            }
        }
        return false;
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeader());
        if (header == null || header.isBlank()) {
            return null;
        }
        String prefix = jwtProperties.getPrefix();
        if (prefix != null && !prefix.isEmpty() && header.startsWith(prefix)) {
            return header.substring(prefix.length()).trim();
        }
        return header.trim();
    }

    private boolean matches(List<String> patterns, String path) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        for (String pattern : patterns) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
