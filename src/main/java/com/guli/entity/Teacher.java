package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 讲师
 */
@Data
@TableName("edu_teacher")
@Schema(description = "讲师")
public class Teacher implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "讲师ID")
    private Long id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "简介")
    private String intro;

    @Schema(description = "资历")
    private String career;

    @Schema(description = "1=讲师 2=高级讲师")
    private Integer level;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "排序")
    private Integer sort;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    @Schema(description = "逻辑删除")
    private Integer isDeleted;
}
