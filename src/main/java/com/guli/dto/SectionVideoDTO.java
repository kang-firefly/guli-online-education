package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 设置小节视频链接
 */
@Data
@Schema(description = "设置小节视频链接入参")
public class SectionVideoDTO implements Serializable {

    @Size(max = 500, message = "链接最多 500 个字符")
    @Schema(description = "B 站视频完整链接，传空字符串表示清除",
            example = "https://www.bilibili.com/video/BV1xx411c7mD?p=1")
    private String videoUrl;
}
