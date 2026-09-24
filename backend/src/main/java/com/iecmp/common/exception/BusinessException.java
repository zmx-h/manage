package com.iecmp.common.exception;

import com.iecmp.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常。业务错误码统一在接口文档 16.1 节定义，抛出后由
 * {@link GlobalExceptionHandler} 转换为统一响应结构。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String msg) {
        super(msg);
        this.code = resultCode.getCode();
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
