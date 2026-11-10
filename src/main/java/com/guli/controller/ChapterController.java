package com.guli.controller;

import com.guli.common.Result;
import com.guli.dto.ChapterSaveDTO;
import com.guli.dto.SectionVideoDTO;
import com.guli.service.ChapterService;
import com.guli.vo.ChapterVO;
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
 * 课程章节（发布向导第 ② 步）
 */
@Tag(name = "06-课程章节")
@RestController
@RequestMapping("/admin/chapter")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @Operation(summary = "某课程的完整大纲（章节 + 小节）")
    @GetMapping("/list/{courseId}")
    public Result<List<ChapterVO>> list(@PathVariable Long courseId) {
        return Result.ok(chapterService.listByCourse(courseId, true));
    }

    @Operation(summary = "新增章节")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ChapterSaveDTO dto) {
        return Result.ok("新增成功", chapterService.create(dto));
    }

    @Operation(summary = "修改章节")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ChapterSaveDTO dto) {
        chapterService.update(dto);
        return Result.ok("修改成功", null);
    }

    @Operation(summary = "删除章节（级联删除小节）")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        chapterService.remove(id);
        return Result.ok("删除成功", null);
    }

    @Operation(summary = "设置/清除小节视频链接（仅支持 B 站完整链接）")
    @PutMapping("/section/{sectionId}/video")
    public Result<Void> setSectionVideo(@PathVariable Long sectionId,
                                        @RequestBody SectionVideoDTO dto) {
        chapterService.setSectionVideo(sectionId, dto.getVideoUrl());
        return Result.ok("已保存", null);
    }
}
