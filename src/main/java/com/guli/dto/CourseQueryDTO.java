package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 课程分页查询入参
 */
@Data
@Schema(description = "课程分页查询入参")
public class CourseQueryDTO implements Serializable {

    @Min(value = 1, message = "页码从 1 开始")
    private Long page = 1L;

    @Min(value = 1, message = "每页至少 1 条")
    @Max(value = 100, message = "每页最多 100 条")
    private Long size = 10L;

    @Schema(description = "课程标题（模糊）")
    private String title;

    @Schema(description = "讲师ID")
    private Long teacherId;

    @Schema(description = "二级分类ID")
    private Long subjectId;

    @Schema(description = "0=未发布 1=已发布")
    private Integer status;
}
