package com.guli.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 */
@Data
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "状态码")
    private Integer code;

    @Schema(description = "提示信息")
    private String message;

    @Schema(description = "数据")
    private T data;

    public Result() {
    }

    public Result(Boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return new Result<>(true, ResultCode.SUCCESS.getCode(), "OK", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(true, ResultCode.SUCCESS.getCode(), "OK", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(true, ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(false, ResultCode.BUSINESS_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(false, code, message, null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(false, resultCode.getCode(), resultCode.getMessage(), null);
    }
}
