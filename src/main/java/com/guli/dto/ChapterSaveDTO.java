package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 章节保存入参
 */
@Data
@Schema(description = "章节保存入参")
public class ChapterSaveDTO implements Serializable {

    @Schema(description = "章节ID，修改时必填")
    private Long id;

    @NotNull(message = "缺少课程ID")
    @Schema(description = "课程ID")
    private Long courseId;

    @NotBlank(message = "章节标题不能为空")
    @Size(max = 50, message = "章节标题不能超过 50 个字符")
    @Schema(description = "章节标题", example = "第一章 起步")
    private String title;

    @Schema(description = "排序", example = "1")
    private Integer sort = 0;
}
