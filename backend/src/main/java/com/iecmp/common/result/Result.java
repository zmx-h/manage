package com.iecmp.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结构（接口文档 2.3 节）。
 *
 * <pre>
 * { "code": 200, "data": {...}, "msg": "success" }
 * </pre>
 *
 * <p>HTTP 状态码统一为 200，业务结果全部由 {@code code} 表达。
 *
 * @param <T> 业务数据类型
 */
@Data
@Schema(description = "统一响应结构")
public class Result<T> implements Serializable {

    @Schema(description = "业务状态码，200 表示成功", example = "200")
    private Integer code;

    @Schema(description = "业务数据，失败时为 null")
    private T data;

    @Schema(description = "提示信息，成功为 success", example = "success")
    private String msg;

    public Result() {
    }

    public Result(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), null, ResultCode.SUCCESS.getMsg());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), data, ResultCode.SUCCESS.getMsg());
    }

    public static <T> Result<T> success(T data, String msg) {
        return new Result<>(ResultCode.SUCCESS.getCode(), data, msg);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), null, resultCode.getMsg());
    }

    public static <T> Result<T> fail(ResultCode resultCode, String msg) {
        return new Result<>(resultCode.getCode(), null, msg);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, null, msg);
    }

    /**
     * 骨架占位：Controller 方法体尚未实现时统一返回本结果。
     *
     * <p>实现某个接口时，把 {@code Result.todo("x-xx")} 换成真实逻辑即可；
     * 全局搜索 {@code Result.todo} 可以看到全部待实现接口。
     *
     * @param apiId 接口编号，如 {@code 1-14}
     */
    public static <T> Result<T> todo(String apiId) {
        return new Result<>(ResultCode.SYSTEM_ERROR.getCode(), null,
                "接口 " + apiId + " 尚未实现（骨架占位）");
    }
}
