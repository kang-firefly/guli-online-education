package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 章节展示对象（含小节）
 */
@Data
@Schema(description = "课程章节")
public class ChapterVO implements Serializable {

    private Long id;

    private Long courseId;

    private String title;

    private Integer sort;

    @Schema(description = "章下小节")
    private List<SectionVO> sections = new ArrayList<>();
}
