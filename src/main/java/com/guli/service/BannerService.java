package com.guli.service;

import com.guli.dto.BannerSaveDTO;
import com.guli.vo.BannerVO;

import java.util.List;

/**
 * 首页轮播维护（后台）
 */
public interface BannerService {

    /** 轮播列表，按 sort 升序 */
    List<BannerVO> list();

    BannerVO create(BannerSaveDTO dto);

    BannerVO update(Long id, BannerSaveDTO dto);

    /** 逻辑删除 */
    void remove(Long id);
}
