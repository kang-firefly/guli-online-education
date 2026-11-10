package com.guli.service.impl;

import com.guli.common.BusinessException;
import com.guli.dto.SectionSaveDTO;
import com.guli.entity.Chapter;
import com.guli.entity.Section;
import com.guli.mapper.ChapterMapper;
import com.guli.mapper.SectionMapper;
import com.guli.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 小节服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionMapper sectionMapper;
    private final ChapterMapper chapterMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SectionSaveDTO dto) {
        Chapter chapter = chapterMapper.selectById(dto.getChapterId());
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }
        if (!chapter.getCourseId().equals(dto.getCourseId())) {
            throw new BusinessException("章节不属于该课程");
        }
        Section entity = new Section();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(null);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        sectionMapper.insert(entity);
        log.info("新增小节 id={} chapterId={}", entity.getId(), dto.getChapterId());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SectionSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("修改小节时 id 不能为空");
        }
        Section exists = sectionMapper.selectById(dto.getId());
        if (exists == null) {
            throw new BusinessException("小节不存在");
        }
        Section entity = new Section();
        BeanUtils.copyProperties(dto, entity);
        sectionMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        Section exists = sectionMapper.selectById(id);
        if (exists == null) {
            throw new BusinessException("小节不存在");
        }
        sectionMapper.deleteById(id);
        log.info("删除小节 id={}", id);
    }
}
