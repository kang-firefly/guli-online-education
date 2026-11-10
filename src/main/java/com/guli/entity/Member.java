package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 前台学员
 */
@Data
@TableName("ucenter_member")
public class Member implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String nickname;

    private String mobile;

    @JsonIgnore
    private String password;

    private String avatar;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Integer isDeleted;
}
