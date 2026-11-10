package com.guli.controller;

import com.guli.common.Result;
import com.guli.dto.LoginDTO;
import com.guli.dto.RegisterDTO;
import com.guli.service.MemberService;
import com.guli.util.IpUtil;
import com.guli.vo.LoginVO;
import com.guli.vo.MemberVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台会员：注册 / 登录 / 当前用户
 */
@Tag(name = "13-前台会员")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "注册（成功即登录）")
    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.ok("注册成功", memberService.register(dto));
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        return Result.ok(memberService.login(dto, IpUtil.getIp(request), request.getHeader("User-Agent")));
    }

    @Operation(summary = "当前登录学员")
    @GetMapping("/me")
    public Result<MemberVO> me() {
        return Result.ok(memberService.me());
    }
}
