package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程分类（两级树）
 */
@Data
@TableName("edu_subject")
@Schema(description = "课程分类")
public class Subject implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "分类名称")
    private String title;

    @Schema(description = "父ID，0=一级分类")
    private Long parentId;

    @Schema(description = "排序")
    private Integer sort;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
