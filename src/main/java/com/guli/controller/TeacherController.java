package com.guli.controller;

import com.guli.common.PageResult;
import com.guli.common.Result;
import com.guli.dto.TeacherQueryDTO;
import com.guli.dto.TeacherSaveDTO;
import com.guli.service.TeacherService;
import com.guli.vo.TeacherVO;
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

import java.util.List;

/**
 * 讲师管理（后台）
 */
@Tag(name = "03-讲师管理")
@RestController
@RequestMapping("/admin/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "分页查询讲师")
    @GetMapping("/page")
    public Result<PageResult<TeacherVO>> page(@ParameterObject TeacherQueryDTO query) {
        return Result.ok(teacherService.page(query));
    }

    @Operation(summary = "全部讲师（下拉选择用）")
    @GetMapping("/all")
    public Result<List<TeacherVO>> all() {
        return Result.ok(teacherService.listAll());
    }

    @Operation(summary = "讲师详情")
    @GetMapping("/{id}")
    public Result<TeacherVO> detail(@PathVariable Long id) {
        return Result.ok(teacherService.getDetail(id));
    }

    @Operation(summary = "新增讲师")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody TeacherSaveDTO dto) {
        return Result.ok("新增成功", teacherService.create(dto));
    }

    @Operation(summary = "修改讲师")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody TeacherSaveDTO dto) {
        teacherService.update(dto);
        return Result.ok("修改成功", null);
    }

    @Operation(summary = "删除讲师")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        teacherService.remove(id);
        return Result.ok("删除成功", null);
    }

    @Operation(summary = "批量删除讲师")
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        teacherService.removeBatch(ids);
        return Result.ok("删除成功", null);
    }
}
