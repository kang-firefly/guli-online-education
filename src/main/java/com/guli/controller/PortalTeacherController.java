package com.guli.controller;

import com.guli.common.PageResult;
import com.guli.common.Result;
import com.guli.service.TeacherService;
import com.guli.vo.TeacherDetailVO;
import com.guli.vo.TeacherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台讲师
 */
@Tag(name = "11-前台讲师")
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class PortalTeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "讲师列表（高级讲师优先）")
    @GetMapping("/page")
    public Result<PageResult<TeacherVO>> page(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "8") Long size) {
        return Result.ok(teacherService.portalPage(page, size));
    }

    @Operation(summary = "讲师详情（含名下已发布课程）")
    @GetMapping("/{id}")
    public Result<TeacherDetailVO> detail(@PathVariable Long id) {
        return Result.ok(teacherService.portalDetail(id));
    }
}
