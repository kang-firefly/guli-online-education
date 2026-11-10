package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 课程分类保存入参
 */
@Data
@Schema(description = "课程分类保存入参")
public class SubjectSaveDTO implements Serializable {

    @Schema(description = "分类ID，修改时必填")
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 20, message = "分类名称不能超过 20 个字符")
    @Schema(description = "分类名称", example = "Vue")
    private String title;

    @Schema(description = "父ID，0 表示一级分类", example = "1")
    private Long parentId = 0L;

    @Schema(description = "排序，越小越前", example = "1")
    private Integer sort = 0;
}
