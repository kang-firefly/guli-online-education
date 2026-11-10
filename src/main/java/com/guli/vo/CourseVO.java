package com.guli.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程列表展示对象
 */
@Data
@Schema(description = "课程列表项")
public class CourseVO implements Serializable {

    private Long id;

    @Schema(description = "课程标题")
    private String title;

    private Long teacherId;

    @Schema(description = "讲师姓名")
    private String teacherName;

    private Long subjectId;

    @Schema(description = "二级分类名")
    private String subjectTitle;

    private Long subjectParentId;

    @Schema(description = "一级分类名")
    private String subjectParentTitle;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "课时数")
    private Integer lessonNum;

    @Schema(description = "封面")
    private String cover;

    @Schema(description = "0=未发布 1=已发布")
    private Integer status;

    private Long buyCount;

    private Long viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;
}
