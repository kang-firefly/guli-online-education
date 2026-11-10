package com.guli.controller;

import com.guli.common.Result;
import com.guli.dto.SubjectSaveDTO;
import com.guli.service.SubjectService;
import com.guli.vo.SubjectTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * 课程分类管理（两级树）
 */
@Tag(name = "04-课程分类")
@RestController
@RequestMapping("/admin/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(summary = "两级分类树")
    @GetMapping("/tree")
    public Result<List<SubjectTreeVO>> tree() {
        return Result.ok(subjectService.tree());
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SubjectSaveDTO dto) {
        return Result.ok("新增成功", subjectService.create(dto));
    }

    @Operation(summary = "修改分类")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SubjectSaveDTO dto) {
        subjectService.update(dto);
        return Result.ok("修改成功", null);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        subjectService.remove(id);
        return Result.ok("删除成功", null);
    }
}
