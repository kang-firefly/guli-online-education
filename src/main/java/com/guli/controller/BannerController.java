package com.guli.controller;

import com.guli.common.Result;
import com.guli.dto.BannerSaveDTO;
import com.guli.service.BannerService;
import com.guli.vo.BannerVO;
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
 * 首页轮播维护（后台）
 */
@Tag(name = "06-首页运营")
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "轮播列表（按排序号升序）")
    @GetMapping("/list")
    public Result<List<BannerVO>> list() {
        return Result.ok(bannerService.list());
    }

    @Operation(summary = "新增轮播")
    @PostMapping
    public Result<BannerVO> create(@Valid @RequestBody BannerSaveDTO dto) {
        return Result.ok("新增成功", bannerService.create(dto));
    }

    @Operation(summary = "修改轮播")
    @PutMapping("/{id}")
    public Result<BannerVO> update(@PathVariable Long id, @Valid @RequestBody BannerSaveDTO dto) {
        return Result.ok("修改成功", bannerService.update(id, dto));
    }

    @Operation(summary = "删除轮播（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        bannerService.remove(id);
        return Result.ok("删除成功", null);
    }
}
