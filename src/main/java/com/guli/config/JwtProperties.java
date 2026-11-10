package com.guli.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "guli.jwt")
public class JwtProperties {

    /** 签名密钥，HS256 要求至少 32 字节 */
    private String secret;

    /** 过期时间（分钟） */
    private Long expireMinutes = 720L;
}
