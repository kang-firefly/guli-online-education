package com.guli.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置（真实值放在 application-local.yml，已被 git 忽略）
 */
@Data
@Component
@ConfigurationProperties(prefix = "guli.oss")
public class OssProperties {

    /** 例如 https://oss-cn-beijing.aliyuncs.com */
    private String endpoint;

    private String bucket;

    /** 访问前缀，例如 https://gulia1.oss-cn-beijing.aliyuncs.com */
    private String urlPrefix;

    private String accessKeyId;

    private String accessKeySecret;
}
