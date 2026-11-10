package com.guli.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guli.common.BusinessException;
import com.guli.common.PageResult;
import com.guli.dto.TeacherQueryDTO;
import com.guli.dto.TeacherSaveDTO;
import com.guli.entity.Course;
import com.guli.entity.Teacher;
import com.guli.mapper.CourseMapper;
import com.guli.mapper.TeacherMapper;
import com.guli.service.CourseService;
import com.guli.service.TeacherService;
import com.guli.vo.TeacherDetailVO;
import com.guli.vo.TeacherVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 讲师服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final CourseService courseService;

    @Override
    public PageResult<TeacherVO> page(TeacherQueryDTO query) {
        LambdaQueryWrapper<Teacher> wrapper = Wrappers.<Teacher>lambdaQuery()
                .like(StringUtils.hasText(query.getName()), Teacher::getName, query.getName())
                .eq(query.getLevel() != null, Teacher::getLevel, query.getLevel())
                .orderByAsc(Teacher::getSort)
                .orderByDesc(Teacher::getId);

        Page<Teacher> page = teacherMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<TeacherVO> records = page.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<TeacherVO> listAll() {
        List<Teacher> list = teacherMapper.selectList(
                Wrappers.<Teacher>lambdaQuery()
                        .orderByAsc(Teacher::getSort)
                        .orderByAsc(Teacher::getId));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    public TeacherVO getDetail(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("讲师不存在");
        }
        return toVO(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TeacherSaveDTO dto) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(dto, teacher);
        teacher.setId(null);
        fillDefault(teacher);
        teacherMapper.insert(teacher);
        log.info("新增讲师 id={} name={}", teacher.getId(), teacher.getName());
        return teacher.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TeacherSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("修改讲师时 id 不能为空");
        }
        Teacher exists = teacherMapper.selectById(dto.getId());
        if (exists == null) {
            throw new BusinessException("讲师不存在");
        }
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(dto, teacher);
        fillDefault(teacher);
        teacherMapper.updateById(teacher);
        log.info("修改讲师 id={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        Teacher exists = teacherMapper.selectById(id);
        if (exists == null) {
            throw new BusinessException("讲师不存在");
        }
        // 业务校验：仍有未删除课程在用时禁止删除
        Long courseCount = courseMapper.selectCount(
                Wrappers.<Course>lambdaQuery().eq(Course::getTeacherId, id));
        if (courseCount != null && courseCount > 0) {
            throw new BusinessException("该讲师名下还有 " + courseCount + " 门课程，请先处理课程后再删除");
        }
        teacherMapper.deleteById(id);
        log.info("删除讲师 id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的讲师");
        }
        for (Long id : ids) {
            remove(id);
        }
    }

    // ---------------- 前台门户 ----------------

    @Override
    public PageResult<TeacherVO> portalPage(Long page, Long size) {
        Long p = page == null || page < 1 ? 1L : page;
        Long s = size == null || size < 1 ? 8L : Math.min(size, 50L);
        Page<Teacher> result = teacherMapper.selectPage(
                new Page<>(p, s),
                Wrappers.<Teacher>lambdaQuery()
                        .orderByDesc(Teacher::getLevel)
                        .orderByAsc(Teacher::getSort)
                        .orderByAsc(Teacher::getId));
        List<TeacherVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public TeacherDetailVO portalDetail(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("讲师不存在");
        }
        TeacherDetailVO vo = new TeacherDetailVO();
        BeanUtils.copyProperties(teacher, vo);
        vo.setCourses(courseService.listPublishedByTeacher(id));
        return vo;
    }

    @Override
    public List<TeacherVO> listTop(int limit) {
        Page<Teacher> result = teacherMapper.selectPage(
                new Page<>(1, Math.max(1, limit)),
                Wrappers.<Teacher>lambdaQuery()
                        .orderByDesc(Teacher::getLevel)
                        .orderByAsc(Teacher::getSort)
                        .orderByAsc(Teacher::getId));
        return result.getRecords().stream().map(this::toVO).toList();
    }

    private void fillDefault(Teacher teacher) {        if (teacher.getLevel() == null) {
            teacher.setLevel(1);
        }
        if (teacher.getSort() == null) {
            teacher.setSort(0);
        }
    }

    private TeacherVO toVO(Teacher teacher) {
        TeacherVO vo = new TeacherVO();
        BeanUtils.copyProperties(teacher, vo);
        return vo;
    }
}
