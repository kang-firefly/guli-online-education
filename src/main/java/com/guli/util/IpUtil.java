package com.guli.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * 获取真实客户端 IP（兼容 Nginx 反向代理）
 */
public final class IpUtil {

    private IpUtil() {
    }

    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String h : headers) {
            String v = request.getHeader(h);
            if (StringUtils.hasText(v) && !"unknown".equalsIgnoreCase(v)) {
                int idx = v.indexOf(',');
                return idx > 0 ? v.substring(0, idx).trim() : v.trim();
            }
        }
        return request.getRemoteAddr();
    }
}
