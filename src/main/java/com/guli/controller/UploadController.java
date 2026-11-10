package com.guli.controller;

import com.guli.common.Result;
import com.guli.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传
 * <p>Controller 只负责：接收参数 → 调用 service → 封装统一返回，不含任何业务逻辑。
 */
@Tag(name = "01-文件上传")
@RestController
@RequestMapping("/admin/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;

    @Operation(summary = "上传图片到 OSS")
    @PostMapping("/image")
    public Result<String> uploadImage(
            @Parameter(description = "图片文件，≤5MB，jpg/png/gif/webp/bmp")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务目录：avatar / cover / banner")
            @RequestParam(value = "dir", defaultValue = "common") String dir) {
        return Result.ok(fileService.uploadImage(file, dir));
    }
}
