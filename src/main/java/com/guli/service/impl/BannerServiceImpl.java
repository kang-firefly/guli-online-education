package com.guli.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.guli.common.BusinessException;
import com.guli.dto.BannerSaveDTO;
import com.guli.entity.Banner;
import com.guli.mapper.BannerMapper;
import com.guli.service.BannerService;
import com.guli.vo.BannerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 首页轮播维护实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public List<BannerVO> list() {
        List<Banner> rows = bannerMapper.selectList(
                Wrappers.<Banner>lambdaQuery()
                        .orderByAsc(Banner::getSort)
                        .orderByAsc(Banner::getId));
        return rows.stream().map(this::toVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BannerVO create(BannerSaveDTO dto) {
        Banner banner = new Banner();
        apply(banner, dto);
        bannerMapper.insert(banner);
        log.info("新增轮播 id={} title={}", banner.getId(), banner.getTitle());
        return toVO(banner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BannerVO update(Long id, BannerSaveDTO dto) {
        Banner banner = requireBanner(id);
        apply(banner, dto);
        bannerMapper.updateById(banner);
        return toVO(banner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        requireBanner(id);
        bannerMapper.deleteById(id);   // isDeleted 逻辑删除，全局配置生效
        log.info("删除轮播 id={}", id);
    }

    private void apply(Banner banner, BannerSaveDTO dto) {
        banner.setTitle(dto.getTitle().trim());
        banner.setImageUrl(dto.getImageUrl().trim());
        banner.setLinkUrl(dto.getLinkUrl() == null ? null : dto.getLinkUrl().trim());
        banner.setSort(dto.getSort() == null ? 0 : dto.getSort());
    }

    private Banner requireBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("轮播图不存在或已删除");
        }
        return banner;
    }

    private BannerVO toVO(Banner b) {
        BannerVO vo = new BannerVO();
        vo.setId(b.getId());
        vo.setTitle(b.getTitle());
        vo.setImageUrl(b.getImageUrl());
        vo.setLinkUrl(b.getLinkUrl());
        vo.setSort(b.getSort());
        return vo;
    }
}
