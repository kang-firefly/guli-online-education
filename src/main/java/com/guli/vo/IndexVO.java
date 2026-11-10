package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页聚合数据
 */
@Data
@Schema(description = "首页聚合数据")
public class IndexVO implements Serializable {

    @Schema(description = "轮播图")
    private List<BannerVO> banners = new ArrayList<>();

    @Schema(description = "热门课程（按购买数）")
    private List<CourseVO> hotCourses = new ArrayList<>();

    @Schema(description = "名师（高级讲师优先）")
    private List<TeacherVO> hotTeachers = new ArrayList<>();

    @Schema(description = "分类（一级带二级）")
    private List<SubjectTreeVO> subjects = new ArrayList<>();

    @Schema(description = "已发布课程总数")
    private Long courseTotal = 0L;

    @Schema(description = "讲师总数")
    private Long teacherTotal = 0L;
}
