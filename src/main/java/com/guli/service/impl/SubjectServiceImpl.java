package com.guli.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.guli.common.BusinessException;
import com.guli.dto.SubjectSaveDTO;
import com.guli.entity.Course;
import com.guli.entity.Subject;
import com.guli.mapper.CourseMapper;
import com.guli.mapper.SubjectMapper;
import com.guli.service.SubjectService;
import com.guli.vo.SubjectTreeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程分类服务实现：只有两级，parentId = 0 为一级
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectMapper subjectMapper;
    private final CourseMapper courseMapper;

    @Override
    public List<SubjectTreeVO> tree() {
        List<Subject> all = subjectMapper.selectList(
                Wrappers.<Subject>lambdaQuery()
                        .orderByAsc(Subject::getSort)
                        .orderByAsc(Subject::getId));

        List<SubjectTreeVO> roots = new ArrayList<>();
        Map<Long, List<Subject>> byParent = all.stream()
                .filter(s -> s.getParentId() != null && s.getParentId() != 0)
                .collect(Collectors.groupingBy(Subject::getParentId));

        for (Subject s : all) {
            if (s.getParentId() != null && s.getParentId() == 0) {
                SubjectTreeVO node = toVO(s);
                List<Subject> children = byParent.getOrDefault(s.getId(), List.of());
                children.stream()
                        .sorted(Comparator.comparing(Subject::getSort, Comparator.nullsLast(Integer::compareTo))
                                .thenComparing(Subject::getId))
                        .forEach(c -> node.getChildren().add(toVO(c)));
                roots.add(node);
            }
        }
        return roots;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SubjectSaveDTO dto) {
        Long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
        if (parentId != 0) {
            Subject parent = subjectMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
            if (parent.getParentId() != null && parent.getParentId() != 0) {
                throw new BusinessException("只支持两级分类，不能在二级分类下再建子类");
            }
        }
        assertTitleUnique(dto.getTitle(), parentId, null);

        Subject entity = new Subject();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        entity.setParentId(parentId);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        subjectMapper.insert(entity);
        log.info("新增分类 id={} title={} parentId={}", entity.getId(), entity.getTitle(), parentId);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SubjectSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("修改分类时 id 不能为空");
        }
        Subject exists = subjectMapper.selectById(dto.getId());
        if (exists == null) {
            throw new BusinessException("分类不存在");
        }

        // 已有子分类的一级分类不允许改成二级（会产生三层）
        Long childCount = subjectMapper.selectCount(
                Wrappers.<Subject>lambdaQuery().eq(Subject::getParentId, dto.getId()));
        Long parentId = dto.getParentId() == null ? exists.getParentId() : dto.getParentId();
        if (childCount != null && childCount > 0 && parentId != 0) {
            throw new BusinessException("该分类下还有子分类，不能改为二级分类");
        }
        assertTitleUnique(dto.getTitle(), parentId, dto.getId());

        Subject entity = new Subject();
        BeanUtils.copyProperties(dto, entity);
        entity.setParentId(parentId);
        subjectMapper.updateById(entity);
        log.info("修改分类 id={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        Subject exists = subjectMapper.selectById(id);
        if (exists == null) {
            throw new BusinessException("分类不存在");
        }
        Long childCount = subjectMapper.selectCount(
                Wrappers.<Subject>lambdaQuery().eq(Subject::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("该分类下还有 " + childCount + " 个子分类，请先删除子分类");
        }
        // 一级分类被课程引用时也不允许删除
        Long courseCount = courseMapper.selectCount(
                Wrappers.<Course>lambdaQuery()
                        .eq(Course::getSubjectId, id)
                        .or()
                        .eq(Course::getSubjectParentId, id));
        if (courseCount != null && courseCount > 0) {
            throw new BusinessException("该分类已被 " + courseCount + " 门课程使用，不能删除");
        }
        subjectMapper.deleteById(id);
        log.info("删除分类 id={}", id);
    }

    private void assertTitleUnique(String title, Long parentId, Long excludeId) {
        Long count = subjectMapper.selectCount(
                Wrappers.<Subject>lambdaQuery()
                        .eq(Subject::getTitle, title)
                        .eq(Subject::getParentId, parentId)
                        .ne(excludeId != null, Subject::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException("同级下已存在同名分类：" + title);
        }
    }

    private SubjectTreeVO toVO(Subject s) {
        SubjectTreeVO vo = new SubjectTreeVO();
        BeanUtils.copyProperties(s, vo);
        return vo;
    }
}
