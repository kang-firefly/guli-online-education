package com.guli.service;

import com.guli.common.PageResult;
import com.guli.dto.CourseQueryDTO;
import com.guli.dto.CourseSaveDTO;
import com.guli.dto.PortalCourseQueryDTO;
import com.guli.vo.CourseDetailVO;
import com.guli.vo.CourseVO;

/**
 * 课程服务（发布向导 ① 基本信息 / ② 大纲 / ③ 发布）
 */
public interface CourseService {

    PageResult<CourseVO> page(CourseQueryDTO query);

    /** 详情：基本信息 + 简介 + 章节小节 */
    CourseDetailVO detail(Long id);

    /** 新建课程（草稿），返回主键 */
    Long create(CourseSaveDTO dto);

    /** 修改基本信息与简介 */
    void update(CourseSaveDTO dto);

    /** 发布：校验信息与大纲完整性后置 status=1 */
    void publish(Long id);

    /** 下架：status 回到 0 */
    void offline(Long id);

    /** 删除：逻辑删除课程，并清理其简介与大纲 */
    void remove(Long id);

    /* ---------------- 前台门户 ---------------- */

    /** 前台课程列表：只返回已发布，支持分类 / 关键字 / 排序 */
    PageResult<CourseVO> portalPage(PortalCourseQueryDTO query);

    /** 前台课程详情：只返回已发布，并累加浏览数 */
    CourseDetailVO portalDetail(Long id);

    /** 某讲师名下已发布的课程（讲师详情页用） */
    java.util.List<CourseVO> listPublishedByTeacher(Long teacherId);

    /** 首页热门课程：按购买数取前 N 门已发布课程 */
    java.util.List<CourseVO> hotCourses(int limit);
}
