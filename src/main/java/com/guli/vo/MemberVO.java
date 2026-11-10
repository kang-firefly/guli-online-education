package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 前台会员信息（不含密码）
 */
@Data
@Schema(description = "前台会员")
public class MemberVO implements Serializable {

    private Long id;

    private String nickname;

    @Schema(description = "手机号，中间四位打码")
    private String mobile;

    private String avatar;
}
