package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台课程列表查询入参
 */
@Data
@Schema(description = "前台课程查询入参")
public class PortalCourseQueryDTO implements Serializable {

    @Min(value = 1, message = "页码从 1 开始")
    private Long page = 1L;

    @Min(value = 1, message = "每页至少 1 条")
    @Max(value = 50, message = "每页最多 50 条")
    private Long size = 8L;

    @Schema(description = "一级分类ID")
    private Long subjectParentId;

    @Schema(description = "二级分类ID")
    private Long subjectId;

    @Schema(description = "标题关键字")
    private String keyword;

    /**
     * latest / price_asc / price_desc / hot
     */
    @Schema(description = "排序：latest 最新 / price_asc 价格升 / price_desc 价格降 / hot 最热", example = "latest")
    private String sort = "latest";
}
