package com.guli.config;

import com.guli.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：跨域 + 登录拦截
 * <p>跨域在前后端分离开发阶段放开，生产环境由 Nginx 同源代理规避。
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final JwtUtil jwtUtil;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ---------- 后台：除登录外全部需要管理员令牌 ----------
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/error",
                        "/favicon.ico"
                );

        // ---------- 前台订单：必须学员登录 ----------
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/order/**");

        // ---------- 前台会员中心：除登录注册外需要学员令牌 ----------
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/member/**")
                .excludePathPatterns("/api/member/login", "/api/member/register");

        // ---------- 前台浏览类：可选登录 ----------
        // 匿名可访问；带了学员令牌则解析出身份，用于判断"是否已购买"
        registry.addInterceptor(LoginInterceptor.optional(jwtUtil))
                .addPathPatterns("/api/course/**", "/api/section/**");
    }
}
