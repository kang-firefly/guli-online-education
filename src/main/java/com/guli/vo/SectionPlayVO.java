package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 小节播放信息
 */
@Data
@Schema(description = "小节播放信息")
public class SectionPlayVO implements Serializable {

    @Schema(description = "小节 ID")
    private Long sectionId;

    @Schema(description = "小节标题")
    private String title;

    @Schema(description = "所属课程 ID")
    private Long courseId;

    @Schema(description = "课程标题")
    private String courseTitle;

    @Schema(description = "无视频时用于占位的封面")
    private String cover;

    @Schema(description = "bilibili / 空（无视频）")
    private String videoType;

    @Schema(description = "受控的播放器地址；无视频为 null")
    private String embedUrl;

    @Schema(description = "是否被锁定（需购买）")
    private Boolean locked;

    @Schema(description = "锁定原因，供前端提示")
    private String reason;
}
