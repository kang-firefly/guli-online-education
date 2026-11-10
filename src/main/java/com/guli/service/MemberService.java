package com.guli.service;

import com.guli.dto.LoginDTO;
import com.guli.dto.RegisterDTO;
import com.guli.vo.LoginVO;
import com.guli.vo.MemberVO;

/**
 * 前台会员服务（注册 / 登录 / 当前用户）
 */
public interface MemberService {

    /** 注册成功即登录，直接返回令牌 */
    LoginVO register(RegisterDTO dto);

    LoginVO login(LoginDTO dto, String ip, String ua);

    /** 当前登录学员信息 */
    MemberVO me();
}
