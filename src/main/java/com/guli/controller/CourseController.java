package com.guli.controller;

import com.guli.common.PageResult;
import com.guli.common.Result;
import com.guli.dto.CourseQueryDTO;
import com.guli.dto.CourseSaveDTO;
import com.guli.service.CourseService;
import com.guli.vo.CourseDetailVO;
import com.guli.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程管理（发布向导 ① 基本信息 → ② 大纲 → ③ 发布）
 */
@Tag(name = "05-课程管理")
@RestController
@RequestMapping("/admin/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "分页查询课程")
    @GetMapping("/page")
    public Result<PageResult<CourseVO>> page(@ParameterObject CourseQueryDTO query) {
        return Result.ok(courseService.page(query));
    }

    @Operation(summary = "课程详情（含简介与大纲）")
    @GetMapping("/{id}")
    public Result<CourseDetailVO> detail(@PathVariable Long id) {
        return Result.ok(courseService.detail(id));
    }

    @Operation(summary = "新增课程（草稿）")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CourseSaveDTO dto) {
        return Result.ok("保存成功", courseService.create(dto));
    }

    @Operation(summary = "修改课程基本信息")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody CourseSaveDTO dto) {
        courseService.update(dto);
        return Result.ok("保存成功", null);
    }

    @Operation(summary = "发布课程")
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        courseService.publish(id);
        return Result.ok("课程已发布", null);
    }

    @Operation(summary = "下架课程")
    @PutMapping("/{id}/offline")
    public Result<Void> offline(@PathVariable Long id) {
        courseService.offline(id);
        return Result.ok("课程已下架", null);
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        courseService.remove(id);
        return Result.ok("删除成功", null);
    }
}
