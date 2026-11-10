package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 课程基本信息保存入参（三步向导的第 ① 步）
 */
@Data
@Schema(description = "课程保存入参")
public class CourseSaveDTO implements Serializable {

    @Schema(description = "课程ID，修改时必填")
    private Long id;

    @NotBlank(message = "课程标题不能为空")
    @Size(max = 50, message = "课程标题不能超过 50 个字符")
    @Schema(description = "课程标题", example = "Vue 3 从入门到实战")
    private String title;

    @NotNull(message = "请选择讲师")
    @Schema(description = "讲师ID")
    private Long teacherId;

    @NotNull(message = "请选择二级分类")
    @Schema(description = "二级分类ID（一级由后端反查）")
    private Long subjectId;

    @NotNull(message = "请填写价格")
    @DecimalMin(value = "0.00", message = "价格不能为负数")
    @Schema(description = "价格，0 表示免费")
    private BigDecimal price;

    @Schema(description = "课时数")
    private Integer lessonNum = 0;

    @Schema(description = "封面 URL（先调上传接口）")
    private String cover;

    @Size(max = 5000, message = "课程简介不能超过 5000 个字符")
    @Schema(description = "课程简介")
    private String description;
}
