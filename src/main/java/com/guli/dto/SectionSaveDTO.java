package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 小节保存入参
 */
@Data
@Schema(description = "小节保存入参")
public class SectionSaveDTO implements Serializable {

    @Schema(description = "小节ID，修改时必填")
    private Long id;

    @NotNull(message = "缺少课程ID")
    @Schema(description = "课程ID")
    private Long courseId;

    @NotNull(message = "缺少章节ID")
    @Schema(description = "章节ID")
    private Long chapterId;

    @NotBlank(message = "小节标题不能为空")
    @Size(max = 50, message = "小节标题不能超过 50 个字符")
    @Schema(description = "小节标题", example = "创建第一个 Vue 3 项目")
    private String title;

    @Size(max = 20, message = "时长不能超过 20 个字符")
    @Schema(description = "时长，如 12:30", example = "12:30")
    private String duration;

    @Schema(description = "排序", example = "1")
    private Integer sort = 0;
}
