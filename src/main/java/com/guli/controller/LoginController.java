package com.guli.controller;

import com.guli.common.LoginUser;
import com.guli.common.Result;
import com.guli.common.UserContext;
import com.guli.dto.LoginDTO;
import com.guli.service.SystemService;
import com.guli.util.IpUtil;
import com.guli.vo.LoginVO;
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
 * 后台认证：登录 / 退出 / 当前登录人
 */
@Tag(name = "02-后台认证")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class LoginController {

    private final SystemService systemService;

    @Operation(summary = "后台登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String ua = request.getHeader("User-Agent");
        return Result.ok(systemService.login(dto, ip, ua));
    }

    @Operation(summary = "退出登录（JWT 无状态，前端清除 token 即可）")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }

    @Operation(summary = "获取当前登录管理员信息")
    @GetMapping("/info")
    public Result<LoginUser> info() {
        return Result.ok(UserContext.require());
    }
}
