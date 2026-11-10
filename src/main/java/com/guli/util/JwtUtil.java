package com.guli.util;

import com.guli.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具：签发与解析（无状态认证的基础设施，不含业务规则）
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    public static final String CLAIM_USER_ID = "uid";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_NICKNAME = "nickname";
    public static final String CLAIM_USER_TYPE = "utype";

    public static final String TYPE_ADMIN = "admin";
    public static final String TYPE_MEMBER = "member";

    private final JwtProperties props;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /** 签发令牌 */
    public String generate(Long userId, String username, String nickname, String userType) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + props.getExpireMinutes() * 60 * 1000);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_NICKNAME, nickname)
                .claim(CLAIM_USER_TYPE, userType)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    /**
     * 解析令牌，失败（过期/篡改/格式错）抛 JwtException
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 有效期（秒） */
    public Long getExpireSeconds() {
        return props.getExpireMinutes() * 60;
    }
}
