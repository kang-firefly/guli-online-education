package com.guli.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 讲师展示对象（对外不暴露 isDeleted）
 */
@Data
@Schema(description = "讲师")
public class TeacherVO implements Serializable {

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;
}
