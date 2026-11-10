package com.guli.controller;

import com.guli.common.Result;
import com.guli.service.IndexService;
import com.guli.vo.IndexVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台首页
 */
@Tag(name = "10-前台首页")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalIndexController {

    private final IndexService indexService;

    @Operation(summary = "首页聚合数据")
    @GetMapping("/index")
    public Result<IndexVO> index() {
        return Result.ok(indexService.index());
    }
}
