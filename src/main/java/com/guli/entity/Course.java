package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程
 */
@Data
@TableName("edu_course")
@Schema(description = "课程")
public class Course implements Serializable {

    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PUBLISHED = 1;

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "讲师ID")
    private Long teacherId;

    @Schema(description = "二级分类ID")
    private Long subjectId;

    @Schema(description = "一级分类ID")
    private Long subjectParentId;

    @Schema(description = "课程标题")
    private String title;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "课时数")
    private Integer lessonNum;

    @Schema(description = "封面URL")
    private String cover;

    @Schema(description = "0=未发布 1=已发布")
    private Integer status;

    @Schema(description = "购买数")
    private Long buyCount;

    @Schema(description = "浏览数")
    private Long viewCount;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Integer isDeleted;

    /** 非数据库字段：列表页展示用 */
    @TableField(exist = false)
    @Schema(description = "讲师姓名（展示用）")
    private String teacherName;

    @TableField(exist = false)
    @Schema(description = "一级分类名（展示用）")
    private String subjectParentTitle;

    @TableField(exist = false)
    @Schema(description = "二级分类名（展示用）")
    private String subjectTitle;
}
