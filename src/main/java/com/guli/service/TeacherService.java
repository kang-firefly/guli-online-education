package com.guli.service;

import com.guli.common.PageResult;
import com.guli.dto.TeacherQueryDTO;
import com.guli.dto.TeacherSaveDTO;
import com.guli.vo.TeacherDetailVO;
import com.guli.vo.TeacherVO;

import java.util.List;

/**
 * 讲师服务
 */
public interface TeacherService {

    /** 分页 + 条件查询 */
    PageResult<TeacherVO> page(TeacherQueryDTO query);

    /** 全部讲师（用于课程发布时的下拉选择） */
    List<TeacherVO> listAll();

    /** 详情 */
    TeacherVO getDetail(Long id);

    /** 新增，返回主键 */
    Long create(TeacherSaveDTO dto);

    /** 修改 */
    void update(TeacherSaveDTO dto);

    /** 删除（逻辑删除） */
    void remove(Long id);

    /** 批量删除（逻辑删除） */
    void removeBatch(List<Long> ids);

    /* ---------------- 前台门户 ---------------- */

    /** 前台讲师列表：高级讲师优先 */
    PageResult<TeacherVO> portalPage(Long page, Long size);

    /** 前台讲师详情：资料 + 名下已发布课程 */
    TeacherDetailVO portalDetail(Long id);

    /** 首页名师：高级讲师优先取前 N 位 */
    List<TeacherVO> listTop(int limit);
}
