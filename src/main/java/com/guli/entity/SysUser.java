package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 后台账号（只有管理员）
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** 序列化时不返回密码 */
    @JsonIgnore
    private String password;

    private String nickname;

    private String avatar;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Integer isDeleted;
}
