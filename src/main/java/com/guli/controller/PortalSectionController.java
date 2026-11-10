package com.guli.controller;

import com.guli.common.Result;
import com.guli.service.SectionPlayService;
import com.guli.vo.SectionPlayVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小节播放（前台）
 * <p>匿名可访问：免费课直接返回播放地址，付费课按登录态与购买记录决定是否锁定。
 */
@Tag(name = "15-小节播放")
@RestController
@RequestMapping("/api/section")
@RequiredArgsConstructor
public class PortalSectionController {

    private final SectionPlayService sectionPlayService;

    @Operation(summary = "取小节播放信息（含权限判定）")
    @GetMapping("/{id}/play")
    public Result<SectionPlayVO> play(@PathVariable Long id) {
        return Result.ok(sectionPlayService.play(id));
    }
}
