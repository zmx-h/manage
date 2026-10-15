package com.iecmp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解：标注在需要写入 {@code sys_oper_log} 的接口方法上（接口文档 8-12）。
 *
 * <p>建议只标注写操作（新增 / 修改 / 删除 / 审核 / 分配），查询接口不标注，避免日志表膨胀。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperLog {

    /** 所属模块，如「用户管理」。 */
    String module() default "";

    /** 操作描述，如「新增用户」。 */
    String value() default "";

    /** 是否记录请求参数。 */
    boolean saveParam() default true;
}
