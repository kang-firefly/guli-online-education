package com.guli.config;

import com.guli.common.LoginUser;
import com.guli.common.ResultCode;
import com.guli.common.UserContext;
import com.guli.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 登录拦截器：校验 Authorization: Bearer &lt;token&gt;
 *
 * <p>两种模式：
 * <ul>
 *   <li><b>强制</b>（默认）：无 token 或 token 无效一律 401。用于 /admin/**、/api/member/**、/api/order/**</li>
 *   <li><b>可选</b>：有 token 就解析出当前用户，没有也放行（UserContext 为 null）。
 *       用于 /api/course/**、/api/section/** —— 这些接口匿名可看，
 *       但要能识别"已购买"的学员，否则已购用户也会被当成匿名。</li>
 * </ul>
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final boolean optional;

    @Autowired
    public LoginInterceptor(JwtUtil jwtUtil) {
        this(jwtUtil, false);
    }

    private LoginInterceptor(JwtUtil jwtUtil, boolean optional) {
        this.jwtUtil = jwtUtil;
        this.optional = optional;
    }

    /** 构造一个"可选登录"的实例，供浏览类接口使用 */
    public static LoginInterceptor optional(JwtUtil jwtUtil) {
        return new LoginInterceptor(jwtUtil, true);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader(HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(PREFIX)) {
            if (optional) {
                return true;                       // 匿名访问：放行，UserContext 保持空
            }
            writeUnauthorized(response, "未登录，请先登录");
            return false;
        }

        try {
            Claims claims = jwtUtil.parse(header.substring(PREFIX.length()).trim());
            LoginUser user = new LoginUser();
            user.setId(claims.get(JwtUtil.CLAIM_USER_ID, Long.class));
            user.setUsername(claims.get(JwtUtil.CLAIM_USERNAME, String.class));
            user.setNickname(claims.get(JwtUtil.CLAIM_NICKNAME, String.class));
            String type = claims.get(JwtUtil.CLAIM_USER_TYPE, String.class);
            user.setUserType(type == null ? JwtUtil.TYPE_ADMIN : type);

            // 令牌类型必须与接口所属端一致：后台令牌不能当学员令牌用，反之亦然
            String path = request.getRequestURI();
            String expected = path.startsWith("/admin") ? JwtUtil.TYPE_ADMIN : JwtUtil.TYPE_MEMBER;
            if (!expected.equals(user.getUserType())) {
                if (optional) {
                    return true;                   // 可选模式下身份不符，按匿名处理
                }
                writeUnauthorized(response, "当前登录身份无权访问该接口");
                return false;
            }

            UserContext.set(user);
            return true;
        } catch (Exception e) {
            log.debug("token 校验失败: {}", e.getMessage());
            if (optional) {
                return true;                       // 可选模式下 token 过期也按匿名处理
            }
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    /**
     * 直接写回统一错误结构。
     * <p>注意：Spring Boot 4 默认使用 Jackson 3（包名 tools.jackson），
     * 容器里不存在 com.fasterxml 的 ObjectMapper bean，因此这里手工拼装固定结构，不依赖 Jackson。
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String json = "{\"success\":false,\"code\":" + ResultCode.UNAUTHORIZED.getCode()
                + ",\"message\":\"" + message + "\",\"data\":null}";
        response.getWriter().write(json);
    }
}
