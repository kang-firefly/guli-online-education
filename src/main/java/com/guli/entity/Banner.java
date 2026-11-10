package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 首页轮播
 */
@Data
@TableName("crm_banner")
public class Banner implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String imageUrl;

    private String linkUrl;

    private Integer sort;

    private Integer isDeleted;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
