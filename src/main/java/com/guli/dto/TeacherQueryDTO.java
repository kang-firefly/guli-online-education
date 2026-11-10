package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 讲师分页查询入参
 */
@Data
@Schema(description = "讲师分页查询入参")
public class TeacherQueryDTO implements Serializable {

    @Min(value = 1, message = "页码从 1 开始")
    @Schema(description = "页码", example = "1")
    private Long page = 1L;

    @Min(value = 1, message = "每页至少 1 条")
    @Max(value = 100, message = "每页最多 100 条")
    @Schema(description = "每页条数", example = "10")
    private Long size = 10L;

    @Schema(description = "姓名（模糊匹配）", example = "张")
    private String name;

    @Schema(description = "级别：1=讲师 2=高级讲师", example = "2")
    private Integer level;
}
