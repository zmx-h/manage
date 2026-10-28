package com.iecmp.common.interceptor;

import com.iecmp.common.context.LoginUser;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token == null || token.trim().isEmpty()) {
            System.out.println(">>> 没带 Token，拦截！");
            response.setStatus(401);
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        System.out.println(">>> 拦截器收到的纯 Token: [" + token + "]");

        try {
            Claims claims = jwtUtil.parseToken(token);

            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                System.out.println(">>> 传了 RefreshToken，拦截！");
                response.setStatus(401);
                return false;
            }

            System.out.println(">>> 校验通过！当前用户ID：" + claims.get("userId"));

            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);

            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(userId);
            loginUser.setUsername(username);
            loginUser.setSuperAdmin(false);
            loginUser.setRoles(new java.util.HashSet<>());
            loginUser.setPermissions(new java.util.HashSet<>());

            UserContext.set(loginUser);

            return true;
        } catch (Exception e) {
            System.out.println(">>> Token 解析失败，原因：" + e.getMessage());
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}