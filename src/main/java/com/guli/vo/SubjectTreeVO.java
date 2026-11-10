package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程分类树节点（两级）
 */
@Data
@Schema(description = "课程分类树节点")
public class SubjectTreeVO implements Serializable {

    private Long id;

    private String title;

    private Long parentId;

    private Integer sort;

    @Schema(description = "子分类，一级分类才有")
    private List<SubjectTreeVO> children = new ArrayList<>();
}
