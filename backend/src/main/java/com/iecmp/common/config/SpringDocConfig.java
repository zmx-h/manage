package com.iecmp.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 接口文档配置（springdoc-openapi）。
 *
 * <p>访问地址：{@code http://localhost:8080/api/swagger-ui/index.html}
 * 文档内容以《IECMP-接口开发文档-v1.6.md》为准，此处仅把已实现的 Controller 映射出来。
 */
@Configuration
public class SpringDocConfig {

    private static final String SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI iecmpOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IECMP 创业中心管理平台 · 接口文档")
                        .description("中原工学院软件学院创新创业中心管理平台。"
                                + "HTTP 状态码统一 200，业务结果由响应体 code 表达。")
                        .version("v1.6")
                        .contact(new Contact().name("IECMP 后端组")))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("登录接口返回的 accessToken，填入时无需再手写 Bearer 前缀")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
    }

    /** 管理端接口分组：{@code /admin/**}。 */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("1-管理端")
                .pathsToMatch("/admin/**")
                .build();
    }

    /** 用户端接口分组：{@code /user/**}。 */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("2-用户端")
                .pathsToMatch("/user/**")
                .build();
    }

    /** 公共接口分组：{@code /common/**}。 */
    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("3-公共接口")
                .pathsToMatch("/common/**")
                .build();
    }
}
