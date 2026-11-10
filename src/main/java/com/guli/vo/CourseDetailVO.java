package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程详情（基本信息 + 简介 + 大纲），课程发布向导 ③ 的确认页用
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程详情")
public class CourseDetailVO extends CourseVO {

    @Schema(description = "课程简介")
    private String description;

    @Schema(description = "章节与小节")
    private List<ChapterVO> chapters = new ArrayList<>();

    @Schema(description = "小节总数")
    private Integer sectionCount = 0;

    @Schema(description = "是否免费（price = 0）")
    private Boolean free;

    @Schema(description = "当前登录学员是否可学（免费 或 已购买）")
    private Boolean purchased;

    @Schema(description = "当前学员对该课程的待支付订单号，无则为 null")
    private String unpaidOrderNo;
}
