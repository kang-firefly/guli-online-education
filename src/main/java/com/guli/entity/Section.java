package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程小节（不含视频，仅标题与时长）
 */
@Data
@TableName("edu_section")
public class Section implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Long chapterId;

    private String title;

    @Schema(description = "时长，如 12:30")
    private String duration;

    /** B 站视频链接，空则该节无视频。注意：不要暴露到前台 VO，播放地址需经权限判定后单独接口返回 */
    private String videoUrl;

    private Integer sort;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
