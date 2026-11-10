package com.guli.service;

import com.guli.dto.LoginDTO;
import com.guli.vo.LoginVO;

/**
 * 系统服务：后台登录相关
 */
public interface SystemService {

    /**
     * 后台登录
     *
     * @param dto     账号密码
     * @param ip      客户端IP（记登录日志）
     * @param ua      User-Agent
     * @return token 与用户信息
     */
    LoginVO login(LoginDTO dto, String ip, String ua);
}
