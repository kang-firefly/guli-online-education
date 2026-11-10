package com.guli.service;

import com.guli.dto.SectionSaveDTO;

/**
 * 课程小节服务
 */
public interface SectionService {

    Long create(SectionSaveDTO dto);

    void update(SectionSaveDTO dto);

    void remove(Long id);
}
