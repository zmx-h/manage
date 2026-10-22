package com.iecmp.common.interceptor;

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
        // 1. 看看前端是不是把放通行证的口袋（Authorization）带过来了
        String token = request.getHeader("Authorization");

        // 2. 如果没带，或者是个空的，直接拒绝
        if (token == null || token.trim().isEmpty()) {
            System.out.println(">>> 没带 Token，拦截！");
            response.setStatus(401); // HTTP 401 表示未登录
            return false; // false 代表不放行
        }

        // 3. 前端传过来的通常带有前缀，比如 "Bearer eyJhbG..."
        // 我们要把前面的 "Bearer " 切掉，只留后面的字符串
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 4. 拿着 Token 去找工具类验明正身
        try {
            Claims claims = jwtUtil.parseToken(token);
            // 打印一下解析出来的信息，证明验章成功
            System.out.println(">>> 校验通过！当前用户ID：" + claims.get("userId"));

            return true; // true 代表放行，让请求去 Controller
        } catch (Exception e) {
            // 如果解析报错，说明 Token 过期、被篡改或者格式不对
            System.out.println(">>> Token 无效或已过期，拦截！");
            response.setStatus(401);
            return false;
        }
    }
}