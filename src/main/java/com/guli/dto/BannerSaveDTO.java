package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Banner 新增 / 修改入参
 */
@Data
@Schema(description = "Banner 保存入参")
public class BannerSaveDTO implements Serializable {

    @NotBlank(message = "标题不能为空")
    @Size(max = 50, message = "标题最多 50 个字符")
    @Schema(description = "标题", example = "Vue 3 从入门到实战")
    private String title;

    @NotBlank(message = "请上传轮播图片")
    @Size(max = 500, message = "图片地址过长")
    @Schema(description = "图片 URL（OSS）")
    private String imageUrl;

    @Size(max = 200, message = "跳转链接最多 200 个字符")
    @Schema(description = "点击跳转地址，可空", example = "/course/1")
    private String linkUrl;

    @Schema(description = "排序号，越小越靠前", example = "1")
    private Integer sort;
}
