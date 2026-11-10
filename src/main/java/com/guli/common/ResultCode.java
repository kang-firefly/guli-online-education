package com.guli.common;

import lombok.Getter;

/**
 * 业务状态码
 */
@Getter
public enum ResultCode {

    SUCCESS(20000, "成功"),
    PARAM_ERROR(20001, "参数错误"),
    UNAUTHORIZED(28004, "未登录或登录已过期"),
    FORBIDDEN(28005, "无访问权限"),
    BUSINESS_ERROR(30001, "业务校验失败"),
    SYSTEM_ERROR(50000, "系统异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
