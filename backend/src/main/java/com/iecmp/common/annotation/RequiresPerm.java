package com.iecmp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限码校验注解（接口文档 2.7 节）。
 *
 * <p>标注在 Controller 方法上，由 {@code AuthInterceptor} 校验当前登录用户是否持有该权限码。
 * 权限码格式为 {@code 模块:资源:操作}，例如 {@code system:user:list}。
 *
 * <p>前台 {@code /user/**} 接口不分配权限码，只要求登录，因此不加本注解。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPerm {

    /** 需要的权限码，多个之间为「或」关系。 */
    String[] value();
}
