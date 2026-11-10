package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 小节展示对象
 */
@Data
@Schema(description = "课程小节")
public class SectionVO implements Serializable {

    private Long id;

    private Long courseId;

    private Long chapterId;

    private String title;

    @Schema(description = "时长，如 12:30")
    private String duration;

    @Schema(description = "是否已配置视频（大纲页显示小图标用）")
    private Boolean hasVideo;

    /** 视频链接：仅后台大纲接口返回，前台详情接口保持 null（non_null 配置下直接不出现） */
    @Schema(description = "视频链接（仅后台接口返回）")
    private String videoUrl;

    private Integer sort;
}
