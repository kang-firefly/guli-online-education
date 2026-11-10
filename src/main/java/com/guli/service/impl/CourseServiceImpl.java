package com.guli.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guli.common.BusinessException;
import com.guli.common.UserContext;
import com.guli.common.PageResult;
import com.guli.dto.CourseQueryDTO;
import com.guli.dto.CourseSaveDTO;
import com.guli.dto.PortalCourseQueryDTO;
import com.guli.entity.Chapter;
import com.guli.entity.Course;
import com.guli.entity.CourseDescription;
import com.guli.entity.Section;
import com.guli.entity.Subject;
import com.guli.entity.Teacher;
import com.guli.mapper.ChapterMapper;
import com.guli.mapper.CourseDescriptionMapper;
import com.guli.mapper.CourseMapper;
import com.guli.mapper.SectionMapper;
import com.guli.mapper.SubjectMapper;
import com.guli.mapper.TeacherMapper;
import com.guli.service.ChapterService;
import com.guli.service.OrderService;
import com.guli.service.CourseService;
import com.guli.vo.ChapterVO;
import com.guli.vo.CourseDetailVO;
import com.guli.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 课程服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseDescriptionMapper descriptionMapper;
    private final TeacherMapper teacherMapper;
    private final SubjectMapper subjectMapper;
    private final ChapterMapper chapterMapper;
    private final SectionMapper sectionMapper;
    private final ChapterService chapterService;
    private final OrderService orderService;

    @Override
    public PageResult<CourseVO> page(CourseQueryDTO query) {
        LambdaQueryWrapper<Course> wrapper = Wrappers.<Course>lambdaQuery()
                .like(StringUtils.hasText(query.getTitle()), Course::getTitle, query.getTitle())
                .eq(query.getTeacherId() != null, Course::getTeacherId, query.getTeacherId())
                .eq(query.getSubjectId() != null, Course::getSubjectId, query.getSubjectId())
                .eq(query.getStatus() != null, Course::getStatus, query.getStatus())
                .orderByDesc(Course::getId);

        Page<Course> page = courseMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        List<CourseVO> records = toVOList(page.getRecords());
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public CourseDetailVO detail(Long id) {
        return buildDetail(requireCourse(id));
    }

    /** 组装详情：基本信息 + 讲师分类名 + 简介 + 大纲 */
    private CourseDetailVO buildDetail(Course course) {
        CourseDetailVO vo = new CourseDetailVO();
        CourseVO base = toVOList(List.of(course)).get(0);
        BeanUtils.copyProperties(base, vo);

        CourseDescription desc = descriptionMapper.selectOne(
                Wrappers.<CourseDescription>lambdaQuery().eq(CourseDescription::getCourseId, course.getId()));
        vo.setDescription(desc == null ? "" : desc.getDescription());

        List<ChapterVO> chapters = chapterService.listByCourse(course.getId());
        vo.setChapters(chapters);
        vo.setSectionCount(chapters.stream().mapToInt(c -> c.getSections().size()).sum());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CourseSaveDTO dto) {
        Teacher teacher = teacherMapper.selectById(dto.getTeacherId());
        if (teacher == null) {
            throw new BusinessException("所选讲师不存在");
        }
        Subject subject = subjectMapper.selectById(dto.getSubjectId());
        if (subject == null) {
            throw new BusinessException("所选课程分类不存在");
        }
        if (subject.getParentId() == null || subject.getParentId() == 0) {
            throw new BusinessException("请选择二级分类");
        }

        Course course = new Course();
        BeanUtils.copyProperties(dto, course);
        course.setId(null);
        course.setSubjectParentId(subject.getParentId());
        course.setStatus(Course.STATUS_DRAFT);
        course.setBuyCount(0L);
        course.setViewCount(0L);
        if (course.getLessonNum() == null) {
            course.setLessonNum(0);
        }
        courseMapper.insert(course);

        saveDescription(course.getId(), dto.getDescription());
        log.info("新建课程 id={} title={}", course.getId(), course.getTitle());
        return course.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CourseSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("修改课程时 id 不能为空");
        }
        Course exists = requireCourse(dto.getId());

        Subject subject = subjectMapper.selectById(dto.getSubjectId());
        if (subject == null) {
            throw new BusinessException("所选课程分类不存在");
        }
        if (subject.getParentId() == null || subject.getParentId() == 0) {
            throw new BusinessException("请选择二级分类");
        }

        // 已发布的课程改了基本信息需要重新发布，这里直接置回未发布
        Course course = new Course();
        BeanUtils.copyProperties(dto, course);
        course.setSubjectParentId(subject.getParentId());
        if (Objects.equals(exists.getStatus(), Course.STATUS_PUBLISHED)) {
            course.setStatus(Course.STATUS_DRAFT);
        }
        courseMapper.updateById(course);

        saveDescription(dto.getId(), dto.getDescription());
        log.info("修改课程 id={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        Course course = requireCourse(id);

        List<String> missing = new ArrayList<>();
        if (!StringUtils.hasText(course.getTitle())) missing.add("课程标题");
        if (course.getTeacherId() == null) missing.add("讲师");
        if (course.getSubjectId() == null) missing.add("课程分类");
        if (!StringUtils.hasText(course.getCover())) missing.add("课程封面");

        CourseDescription desc = descriptionMapper.selectOne(
                Wrappers.<CourseDescription>lambdaQuery().eq(CourseDescription::getCourseId, id));
        if (desc == null || !StringUtils.hasText(desc.getDescription())) missing.add("课程简介");

        Long chapterCount = chapterMapper.selectCount(
                Wrappers.<Chapter>lambdaQuery().eq(Chapter::getCourseId, id));
        Long sectionCount = sectionMapper.selectCount(
                Wrappers.<Section>lambdaQuery().eq(Section::getCourseId, id));
        if (chapterCount == null || chapterCount == 0) missing.add("至少一个章节");
        if (sectionCount == null || sectionCount == 0) missing.add("至少一个小节");

        if (!missing.isEmpty()) {
            throw new BusinessException("课程信息不完整，缺失：" + String.join("、", missing));
        }

        Course update = new Course();
        update.setId(id);
        update.setStatus(Course.STATUS_PUBLISHED);
        courseMapper.updateById(update);
        log.info("发布课程 id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long id) {
        requireCourse(id);
        Course update = new Course();
        update.setId(id);
        update.setStatus(Course.STATUS_DRAFT);
        courseMapper.updateById(update);
        log.info("下架课程 id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        requireCourse(id);
        // 课程逻辑删除后其大纲与简介已无意义，这里直接物理清理，避免孤儿数据
        sectionMapper.delete(Wrappers.<Section>lambdaQuery().eq(Section::getCourseId, id));
        chapterMapper.delete(Wrappers.<Chapter>lambdaQuery().eq(Chapter::getCourseId, id));
        descriptionMapper.delete(Wrappers.<CourseDescription>lambdaQuery().eq(CourseDescription::getCourseId, id));
        courseMapper.deleteById(id);
        log.info("删除课程 id={}", id);
    }

    // ---------------- 前台门户 ----------------

    @Override
    public PageResult<CourseVO> portalPage(PortalCourseQueryDTO query) {
        LambdaQueryWrapper<Course> wrapper = Wrappers.<Course>lambdaQuery()
                .eq(Course::getStatus, Course.STATUS_PUBLISHED)
                .eq(query.getSubjectParentId() != null, Course::getSubjectParentId, query.getSubjectParentId())
                .eq(query.getSubjectId() != null, Course::getSubjectId, query.getSubjectId())
                .like(StringUtils.hasText(query.getKeyword()), Course::getTitle, query.getKeyword());

        String sort = query.getSort() == null ? "latest" : query.getSort();
        switch (sort) {
            case "price_asc" -> wrapper.orderByAsc(Course::getPrice).orderByDesc(Course::getId);
            case "price_desc" -> wrapper.orderByDesc(Course::getPrice).orderByDesc(Course::getId);
            case "hot" -> wrapper.orderByDesc(Course::getBuyCount).orderByDesc(Course::getId);
            default -> wrapper.orderByDesc(Course::getId);
        }

        Page<Course> page = courseMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        return new PageResult<>(toVOList(page.getRecords()), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public CourseDetailVO portalDetail(Long id) {
        Course course = requireCourse(id);
        if (!Objects.equals(course.getStatus(), Course.STATUS_PUBLISHED)) {
            throw new BusinessException("课程不存在或已下架");
        }
        // 浏览数 +1（用 SQL 自增避免并发覆盖）
        courseMapper.update(null, Wrappers.<Course>lambdaUpdate()
                .eq(Course::getId, id)
                .setSql("view_count = view_count + 1"));

        CourseDetailVO vo = buildDetail(course);
        vo.setViewCount((course.getViewCount() == null ? 0 : course.getViewCount()) + 1);

        // 购买状态：免费课对所有人开放；付费课看当前学员是否已购
        java.math.BigDecimal price = course.getPrice() == null ? java.math.BigDecimal.ZERO : course.getPrice();
        boolean free = price.compareTo(java.math.BigDecimal.ZERO) <= 0;
        Long memberId = UserContext.getUserId();      // 未登录时为 null
        vo.setFree(free);
        vo.setPurchased(free || (memberId != null && orderService.hasPaid(memberId, id)));
        vo.setUnpaidOrderNo(memberId == null ? null : orderService.findUnpaidOrderNo(memberId, id));
        return vo;
    }

    @Override
    public List<CourseVO> listPublishedByTeacher(Long teacherId) {
        if (teacherId == null) {
            return List.of();
        }
        List<Course> courses = courseMapper.selectList(
                Wrappers.<Course>lambdaQuery()
                        .eq(Course::getTeacherId, teacherId)
                        .eq(Course::getStatus, Course.STATUS_PUBLISHED)
                        .orderByDesc(Course::getId));
        return toVOList(courses);
    }

    @Override
    public List<CourseVO> hotCourses(int limit) {
        Page<Course> page = courseMapper.selectPage(
                new Page<>(1, Math.max(1, limit)),
                Wrappers.<Course>lambdaQuery()
                        .eq(Course::getStatus, Course.STATUS_PUBLISHED)
                        .orderByDesc(Course::getBuyCount)
                        .orderByDesc(Course::getId));
        return toVOList(page.getRecords());
    }

    // ---------------- 内部方法 ----------------

    private Course requireCourse(Long id) {
        if (id == null) {
            throw new BusinessException("缺少课程ID");
        }
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        return course;
    }

    private void saveDescription(Long courseId, String description) {
        if (!StringUtils.hasText(description)) {
            return;
        }
        CourseDescription exists = descriptionMapper.selectOne(
                Wrappers.<CourseDescription>lambdaQuery().eq(CourseDescription::getCourseId, courseId));
        if (exists == null) {
            CourseDescription entity = new CourseDescription();
            entity.setCourseId(courseId);
            entity.setDescription(description);
            descriptionMapper.insert(entity);
        } else {
            exists.setDescription(description);
            descriptionMapper.updateById(exists);
        }
    }

    /** 批量补齐讲师名与分类名，避免列表 N+1 查询 */
    private List<CourseVO> toVOList(List<Course> courses) {
        if (courses.isEmpty()) {
            return List.of();
        }
        List<Long> teacherIds = courses.stream().map(Course::getTeacherId).filter(Objects::nonNull).distinct().toList();
        List<Long> subjectIds = courses.stream().map(Course::getSubjectId).filter(Objects::nonNull).distinct().toList();

        Map<Long, String> teacherNames = teacherIds.isEmpty() ? Map.of()
                : teacherMapper.selectBatchIds(teacherIds).stream()
                .collect(Collectors.toMap(Teacher::getId, Teacher::getName, (a, b) -> a));
        Map<Long, String> subjectTitles = subjectIds.isEmpty() ? Map.of()
                : subjectMapper.selectBatchIds(subjectIds).stream()
                .collect(Collectors.toMap(Subject::getId, Subject::getTitle, (a, b) -> a));
        Map<Long, String> parentTitles = subjectMapper.selectList(null).stream()
                .collect(Collectors.toMap(Subject::getId, Subject::getTitle, (a, b) -> a));

        return courses.stream().map(c -> {
            CourseVO vo = new CourseVO();
            BeanUtils.copyProperties(c, vo);
            vo.setTeacherName(teacherNames.get(c.getTeacherId()));
            vo.setSubjectTitle(subjectTitles.get(c.getSubjectId()));
            vo.setSubjectParentTitle(parentTitles.get(c.getSubjectParentId()));
            return vo;
        }).collect(Collectors.toCollection(ArrayList::new));
    }
}
