package com.guli.controller;

import com.guli.common.Result;
import com.guli.dto.SectionSaveDTO;
import com.guli.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程小节（发布向导第 ② 步）
 */
@Tag(name = "07-课程小节")
@RestController
@RequestMapping("/admin/section")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @Operation(summary = "新增小节")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SectionSaveDTO dto) {
        return Result.ok("新增成功", sectionService.create(dto));
    }

    @Operation(summary = "修改小节")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SectionSaveDTO dto) {
        sectionService.update(dto);
        return Result.ok("修改成功", null);
    }

    @Operation(summary = "删除小节")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sectionService.remove(id);
        return Result.ok("删除成功", null);
    }
}
