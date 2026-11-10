package com.guli.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.guli.common.BusinessException;
import com.guli.common.UserContext;
import com.guli.dto.LoginDTO;
import com.guli.dto.RegisterDTO;
import com.guli.entity.LoginLog;
import com.guli.entity.Member;
import com.guli.mapper.LoginLogMapper;
import com.guli.mapper.MemberMapper;
import com.guli.service.MemberService;
import com.guli.util.JwtUtil;
import com.guli.vo.LoginVO;
import com.guli.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 前台会员服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final LoginLogMapper loginLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO dto) {
        Long exists = memberMapper.selectCount(
                Wrappers.<Member>lambdaQuery().eq(Member::getMobile, dto.getMobile()));
        if (exists != null && exists > 0) {
            throw new BusinessException("该手机号已注册，请直接登录");
        }
        Member member = new Member();
        member.setNickname(dto.getNickname());
        member.setMobile(dto.getMobile());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        memberMapper.insert(member);
        log.info("会员注册成功 id={} mobile={}", member.getId(), dto.getMobile());
        return issueToken(member);
    }

    @Override
    public LoginVO login(LoginDTO dto, String ip, String ua) {
        Member member = memberMapper.selectOne(
                Wrappers.<Member>lambdaQuery().eq(Member::getMobile, dto.getUsername()));
        if (member == null || !passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            saveLoginLog(dto.getUsername(), ip, ua, 0);
            throw new BusinessException("手机号或密码错误");
        }
        saveLoginLog(member.getMobile(), ip, ua, 1);
        return issueToken(member);
    }

    @Override
    public MemberVO me() {
        Long id = UserContext.require().getId();
        Member member = memberMapper.selectById(id);
        if (member == null) {
            throw new BusinessException("账号不存在");
        }
        MemberVO vo = new MemberVO();
        vo.setId(member.getId());
        vo.setNickname(member.getNickname());
        vo.setMobile(maskMobile(member.getMobile()));
        vo.setAvatar(member.getAvatar());
        return vo;
    }

    private LoginVO issueToken(Member member) {
        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.generate(member.getId(), member.getMobile(), member.getNickname(), JwtUtil.TYPE_MEMBER));
        vo.setExpiresIn(jwtUtil.getExpireSeconds());
        vo.setUserId(member.getId());
        vo.setUsername(member.getMobile());
        vo.setNickname(member.getNickname());
        vo.setAvatar(member.getAvatar());
        return vo;
    }

    private void saveLoginLog(String username, String ip, String ua, int success) {
        try {
            LoginLog entity = new LoginLog();
            entity.setUsername(username);
            entity.setUserType("member");
            entity.setIp(ip);
            entity.setUserAgent(ua);
            entity.setSuccess(success);
            loginLogMapper.insert(entity);
        } catch (Exception e) {
            log.warn("写入会员登录日志失败: {}", e.getMessage());
        }
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
}
