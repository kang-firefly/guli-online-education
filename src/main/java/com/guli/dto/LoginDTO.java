package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 后台登录入参
 */
@Data
@Schema(description = "后台登录入参")
public class LoginDTO implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "登录名", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "admin123")
    private String password;
}
