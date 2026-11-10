package com.guli.controller;

import com.guli.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查：部署后用于验证服务是否存活
 */
@Tag(name = "00-健康检查")
@RestController
@RequestMapping("/api")
public class HealthController {

    @Operation(summary = "服务健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("app", "guli");
        data.put("time", LocalDateTime.now().toString());
        return Result.ok(data);
    }
}
