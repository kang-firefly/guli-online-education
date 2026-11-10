package com.guli.service;

import com.guli.dto.SubjectSaveDTO;
import com.guli.vo.SubjectTreeVO;

import java.util.List;

/**
 * 课程分类服务（两级树）
 */
public interface SubjectService {

    /** 两级树，一级分类携带 children */
    List<SubjectTreeVO> tree();

    /** 新增，返回主键 */
    Long create(SubjectSaveDTO dto);

    /** 修改 */
    void update(SubjectSaveDTO dto);

    /** 删除（存在子分类或被课程引用时拒绝） */
    void remove(Long id);
}
