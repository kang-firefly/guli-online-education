package com.guli.controller;

import com.guli.common.PageResult;
import com.guli.common.Result;
import com.guli.dto.PortalCourseQueryDTO;
import com.guli.service.CourseService;
import com.guli.vo.CourseDetailVO;
import com.guli.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台课程
 */
@Tag(name = "12-前台课程")
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class PortalCourseController {

    private final CourseService courseService;

    @Operation(summary = "课程列表（只返回已发布）")
    @GetMapping("/page")
    public Result<PageResult<CourseVO>> page(@ParameterObject PortalCourseQueryDTO query) {
        return Result.ok(courseService.portalPage(query));
    }

    @Operation(summary = "课程详情（含大纲）")
    @GetMapping("/{id}")
    public Result<CourseDetailVO> detail(@PathVariable Long id) {
        return Result.ok(courseService.portalDetail(id));
    }
}
