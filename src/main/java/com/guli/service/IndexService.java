package com.guli.service;

import com.guli.vo.IndexVO;

/**
 * 首页聚合服务
 */
public interface IndexService {

    /** 首页数据：轮播 + 热门课程 + 名师 + 分类 + 统计数 */
    IndexVO index();
}
