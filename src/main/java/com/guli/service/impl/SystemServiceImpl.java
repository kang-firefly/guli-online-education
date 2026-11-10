package com.guli.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.guli.common.BusinessException;
import com.guli.common.LoginUser;
import com.guli.common.UserContext;
import com.guli.dto.LoginDTO;
import com.guli.entity.LoginLog;
import com.guli.entity.SysUser;
import com.guli.mapper.LoginLogMapper;
import com.guli.mapper.SysUserMapper;
import com.guli.service.SystemService;
import com.guli.util.JwtUtil;
import com.guli.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 后台登录实现：查账号 → BCrypt 校验 → 签发 JWT → 记登录日志
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final SysUserMapper sysUserMapper;
    private final LoginLogMapper loginLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto, String ip, String ua) {
        SysUser user = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, dto.getUsername()));

        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            saveLoginLog(dto.getUsername(), ip, ua, 0);
            // 统一提示，避免暴露"用户是否存在"
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generate(user.getId(), user.getUsername(), user.getNickname(), JwtUtil.TYPE_ADMIN);
        saveLoginLog(user.getUsername(), ip, ua, 1);

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setExpiresIn(jwtUtil.getExpireSeconds());
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        return vo;
    }

    private void saveLoginLog(String username, String ip, String ua, int success) {
        try {
            LoginLog logEntity = new LoginLog();
            logEntity.setUsername(username);
            logEntity.setUserType("admin");
            logEntity.setIp(ip);
            logEntity.setUserAgent(ua);
            logEntity.setSuccess(success);
            loginLogMapper.insert(logEntity);
        } catch (Exception e) {
            // 日志失败不影响登录主流程
            log.warn("写入登录日志失败: {}", e.getMessage());
        }
    }

    /** 供 /admin/info 使用：当前登录人信息（预留） */
    protected LoginUser current() {
        return UserContext.require();
    }
}
