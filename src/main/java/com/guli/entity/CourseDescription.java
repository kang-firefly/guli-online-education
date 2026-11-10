package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程简介（与课程 1:1）
 */
@Data
@TableName("edu_course_description")
public class CourseDescription implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String description;

    private LocalDateTime gmtCreate;
}
