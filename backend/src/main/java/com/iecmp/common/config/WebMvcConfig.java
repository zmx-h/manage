package com.iecmp.common.config;

import com.iecmp.common.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册我们的保安
        registry.addInterceptor(authInterceptor)
                // "/**" 表示拦截所有的请求
                .addPathPatterns("/**")
                // 但是，登录接口不能拦（不然别人连登录都登不了）
                // 你的登录接口是 /user/login （注意：这里的路径不含外层的 /api）
                .excludePathPatterns(
                        "/user/login",
                        "/common/captcha" // 顺便把图形验证码也放行
                );
    }
}