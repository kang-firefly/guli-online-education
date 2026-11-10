package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程章节
 */
@Data
@TableName("edu_chapter")
public class Chapter implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String title;

    private Integer sort;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
