package com.guli.service;

import com.guli.dto.ChapterSaveDTO;
import com.guli.vo.ChapterVO;

import java.util.List;

/**
 * 课程章节服务
 */
public interface ChapterService {

    /** 某课程的完整大纲（章节 + 小节），不含视频链接 */
    List<ChapterVO> listByCourse(Long courseId);

    /** 后台用：同时返回每节的视频链接，供管理员编辑 */
    List<ChapterVO> listByCourse(Long courseId, boolean includeVideo);

    Long create(ChapterSaveDTO dto);

    void update(ChapterSaveDTO dto);

    /** 删除章节，级联删除其下小节 */
    void remove(Long id);

    /** 设置或清除某小节的视频链接（传空表示清除），会校验链接是否为受支持的 B 站地址 */
    void setSectionVideo(Long sectionId, String videoUrl);
}
