package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台注册入参
 */
@Data
@Schema(description = "前台注册入参")
public class RegisterDTO implements Serializable {

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称 2~20 个字符")
    @Schema(description = "昵称", example = "小明")
    private String nickname;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号（登录账号）", example = "13900000001")
    private String mobile;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码 6~20 位")
    @Schema(description = "密码", example = "123456")
    private String password;
}
