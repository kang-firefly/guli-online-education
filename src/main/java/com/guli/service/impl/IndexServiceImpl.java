package com.guli.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.guli.entity.Banner;
import com.guli.entity.Course;
import com.guli.entity.Teacher;
import com.guli.mapper.BannerMapper;
import com.guli.mapper.CourseMapper;
import com.guli.mapper.TeacherMapper;
import com.guli.service.CourseService;
import com.guli.service.IndexService;
import com.guli.service.SubjectService;
import com.guli.service.TeacherService;
import com.guli.vo.BannerVO;
import com.guli.vo.IndexVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页聚合实现：只做编排，具体数据由各领域服务提供
 */
@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {

    private static final int HOT_COURSE_LIMIT = 8;
    private static final int HOT_TEACHER_LIMIT = 4;

    private final BannerMapper bannerMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;

    @Override
    public IndexVO index() {
        IndexVO vo = new IndexVO();

        List<Banner> banners = bannerMapper.selectList(
                Wrappers.<Banner>lambdaQuery()
                        .orderByAsc(Banner::getSort)
                        .orderByAsc(Banner::getId));
        vo.setBanners(banners.stream().map(b -> {
            BannerVO bv = new BannerVO();
            BeanUtils.copyProperties(b, bv);
            return bv;
        }).toList());

        vo.setHotCourses(courseService.hotCourses(HOT_COURSE_LIMIT));
        vo.setHotTeachers(teacherService.listTop(HOT_TEACHER_LIMIT));
        vo.setSubjects(subjectService.tree());
        vo.setCourseTotal(courseMapper.selectCount(
                Wrappers.<Course>lambdaQuery().eq(Course::getStatus, Course.STATUS_PUBLISHED)));
        vo.setTeacherTotal(teacherMapper.selectCount(Wrappers.<Teacher>lambdaQuery()));
        return vo;
    }
}
