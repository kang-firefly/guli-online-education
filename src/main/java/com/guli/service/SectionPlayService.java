package com.guli.service;

import com.guli.vo.SectionPlayVO;

/**
 * 小节播放（前台）
 */
public interface SectionPlayService {

    /**
     * 取播放信息，内部完成权限判定：
     * 免费课放行（含未登录）／付费课需已购买／否则返回锁定态（不返回播放地址）
     */
    SectionPlayVO play(Long sectionId);
}
