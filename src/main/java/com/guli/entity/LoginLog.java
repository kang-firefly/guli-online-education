package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志
 */
@Data
@TableName("sys_login_log")
public class LoginLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** admin / member */
    private String userType;

    private String ip;

    private String userAgent;

    /** 1=成功 0=失败 */
    private Integer success;

    private LocalDateTime loginTime;
}
