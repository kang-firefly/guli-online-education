package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 前台讲师详情（讲师资料 + 其名下已发布课程）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "讲师详情")
public class TeacherDetailVO extends TeacherVO {

    @Schema(description = "该讲师已发布的课程")
    private List<CourseVO> courses = new ArrayList<>();
}
