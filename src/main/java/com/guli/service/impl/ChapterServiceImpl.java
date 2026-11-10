package com.guli.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.guli.common.BusinessException;
import com.guli.dto.ChapterSaveDTO;
import com.guli.entity.Chapter;
import com.guli.entity.Course;
import com.guli.entity.Section;
import com.guli.mapper.ChapterMapper;
import com.guli.mapper.CourseMapper;
import com.guli.mapper.SectionMapper;
import com.guli.util.BilibiliParser;
import com.guli.service.ChapterService;
import com.guli.vo.ChapterVO;
import com.guli.vo.SectionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 章节服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterMapper chapterMapper;
    private final SectionMapper sectionMapper;
    private final CourseMapper courseMapper;

    @Override
    public List<ChapterVO> listByCourse(Long courseId) {
        return listByCourse(courseId, false);
    }

    @Override
    public List<ChapterVO> listByCourse(Long courseId, boolean includeVideo) {
        if (courseId == null) {
            throw new BusinessException("缺少课程ID");
        }
        List<Chapter> chapters = chapterMapper.selectList(
                Wrappers.<Chapter>lambdaQuery()
                        .eq(Chapter::getCourseId, courseId)
                        .orderByAsc(Chapter::getSort)
                        .orderByAsc(Chapter::getId));
        if (chapters.isEmpty()) {
            return List.of();
        }

        List<Long> chapterIds = chapters.stream().map(Chapter::getId).toList();
        List<Section> sections = sectionMapper.selectList(
                Wrappers.<Section>lambdaQuery()
                        .in(Section::getChapterId, chapterIds)
                        .orderByAsc(Section::getSort)
                        .orderByAsc(Section::getId));
        Map<Long, List<Section>> byChapter = sections.stream()
                .collect(Collectors.groupingBy(Section::getChapterId));

        List<ChapterVO> result = new ArrayList<>(chapters.size());
        for (Chapter c : chapters) {
            ChapterVO vo = new ChapterVO();
            BeanUtils.copyProperties(c, vo);
            for (Section s : byChapter.getOrDefault(c.getId(), List.of())) {
                SectionVO sv = new SectionVO();
                BeanUtils.copyProperties(s, sv);
                // 只暴露"有没有视频"，不暴露链接本身
                sv.setHasVideo(s.getVideoUrl() != null && !s.getVideoUrl().isBlank());
                if (includeVideo) {
                    sv.setVideoUrl(s.getVideoUrl());   // 仅后台接口下发
                }
                vo.getSections().add(sv);
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setSectionVideo(Long sectionId, String videoUrl) {
        Section section = sectionMapper.selectById(sectionId);
        if (section == null) {
            throw new BusinessException("小节不存在");
        }
        String url = (videoUrl == null || videoUrl.isBlank()) ? null : videoUrl.trim();
        if (url != null && !BilibiliParser.isSupported(url)) {
            throw new BusinessException(
                    "只支持 B 站视频完整链接，例如 https://www.bilibili.com/video/BV1xx411c7mD（不支持 b23.tv 短链）");
        }
        section.setVideoUrl(url);
        sectionMapper.updateById(section);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ChapterSaveDTO dto) {
        assertCourseExists(dto.getCourseId());
        Chapter entity = new Chapter();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        chapterMapper.insert(entity);
        log.info("新增章节 id={} courseId={}", entity.getId(), dto.getCourseId());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ChapterSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("修改章节时 id 不能为空");
        }
        Chapter exists = chapterMapper.selectById(dto.getId());
        if (exists == null) {
            throw new BusinessException("章节不存在");
        }
        Chapter entity = new Chapter();
        BeanUtils.copyProperties(dto, entity);
        chapterMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        Chapter exists = chapterMapper.selectById(id);
        if (exists == null) {
            throw new BusinessException("章节不存在");
        }
        // 节点删除：章下小节一并移除，避免留下孤儿数据
        int removed = sectionMapper.delete(
                Wrappers.<Section>lambdaQuery().eq(Section::getChapterId, id));
        chapterMapper.deleteById(id);
        log.info("删除章节 id={}，级联删除小节 {} 条", id, removed);
    }

    private void assertCourseExists(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
    }
}
