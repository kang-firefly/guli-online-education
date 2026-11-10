package com.guli.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 当前登录用户（从 JWT 解析而来，随请求线程传递）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {

    private Long id;

    private String username;

    private String nickname;

    /** admin / member */
    private String userType;
}
