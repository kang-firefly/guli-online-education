package com.guli.service.impl;

import com.guli.common.BusinessException;
import com.guli.common.UserContext;
import com.guli.entity.Course;
import com.guli.entity.Section;
import com.guli.mapper.CourseMapper;
import com.guli.mapper.SectionMapper;
import com.guli.service.OrderService;
import com.guli.service.SectionPlayService;
import com.guli.util.BilibiliParser;
import com.guli.vo.SectionPlayVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 小节播放实现
 * <p>权限判定的唯一入口。前端隐藏按钮不算数，必须在这里拦，
 * 否则直接请求 /api/section/{id}/play 就能拿到播放地址。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SectionPlayServiceImpl implements SectionPlayService {

    private final SectionMapper sectionMapper;
    private final CourseMapper courseMapper;
    private final OrderService orderService;

    @Override
    public SectionPlayVO play(Long sectionId) {
        Section section = sectionMapper.selectById(sectionId);
        if (section == null) {
            throw new BusinessException("小节不存在");
        }
        Course course = courseMapper.selectById(section.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        SectionPlayVO vo = new SectionPlayVO();
        vo.setSectionId(section.getId());
        vo.setTitle(section.getTitle());
        vo.setCourseId(course.getId());
        vo.setCourseTitle(course.getTitle());
        vo.setCover(course.getCover());

        // ---------- 权限判定 ----------
        BigDecimal price = course.getPrice() == null ? BigDecimal.ZERO : course.getPrice();
        boolean free = price.compareTo(BigDecimal.ZERO) <= 0;
        Long memberId = UserContext.getUserId();
        boolean bought = memberId != null && orderService.hasPaid(memberId, course.getId());

        if (!free && !bought) {
            vo.setLocked(true);
            vo.setReason(memberId == null ? "登录并购买后即可观看" : "购买后即可观看");
            vo.setVideoType(null);
            vo.setEmbedUrl(null);
            return vo;
        }

        // ---------- 已放行：解析视频 ----------
        vo.setLocked(false);
        String raw = section.getVideoUrl();
        BilibiliParser.Parsed parsed = BilibiliParser.parse(raw);
        if (parsed != null) {
            vo.setVideoType("bilibili");
            // 只返回后端拼装的地址，绝不回传原始字符串
            vo.setEmbedUrl(BilibiliParser.toEmbedUrl(parsed));
        } else if (raw != null && !raw.isBlank()) {
            log.warn("小节 {} 的视频链接无法解析，将显示占位图: {}", sectionId, raw);
        }
        return vo;
    }
}
