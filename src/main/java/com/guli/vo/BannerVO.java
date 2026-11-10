package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 首页轮播项
 */
@Data
@Schema(description = "首页轮播")
public class BannerVO implements Serializable {

    private Long id;

    private String title;

    private String imageUrl;

    private String linkUrl;

    private Integer sort;
}
