package com.iecmp.common.result;

import lombok.Getter;

/**
 * 通用业务状态码（接口文档 2.8 节）。
 *
 * <p>模块业务错误码按号段划分，取值见接口文档 16.1 节：
 * <ul>
 *   <li>10001 ~ 19999 模块一 · 用户与权限</li>
 *   <li>20001 ~ 29999 模块二 · 代码仓库关联</li>
 *   <li>30001 ~ 39999 模块三 · 部门预算</li>
 *   <li>40001 ~ 49999 模块四 · 部门财产</li>
 *   <li>50001 ~ 59999 模块五 · 部门工位</li>
 *   <li>60001 ~ 69999 模块六 · 学长学姐展板</li>
 *   <li>70001 ~ 79999 模块七 · 值日排班</li>
 *   <li>80001 ~ 89999 模块八 · 文件与通用支撑</li>
 * </ul>
 *
 * <p><b>约定</b>：实现各模块时，在本枚举末尾按号段补充常量；常量名用英文大写下划线，
 * 说明文案与接口文档 16.1 节逐字一致，便于前端按 {@code msg} 直接展示。
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "success"),
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /** 按 code 反查通用状态码，查不到返回 {@link #SYSTEM_ERROR}。 */
    public static ResultCode of(Integer code) {
        if (code != null) {
            for (ResultCode item : values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return SYSTEM_ERROR;
    }
}
