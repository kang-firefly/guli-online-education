package com.guli.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.guli.common.BusinessException;
import com.guli.common.ResultCode;
import com.guli.config.OssProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * OSS 客户端封装（基础设施层）
 * <p>只负责"把字节放到 OSS 并返回访问地址"，不做任何业务校验；
 * 类型、大小、目录规则等业务约束由 {@code FileServiceImpl} 承担。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssUtil {

    private final OssProperties props;
    private OSS client;

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(props.getAccessKeyId()) || !StringUtils.hasText(props.getAccessKeySecret())) {
            log.warn("OSS 未配置 accessKeyId/accessKeySecret，图片上传功能不可用");
            return;
        }
        client = new OSSClientBuilder().build(props.getEndpoint(), props.getAccessKeyId(), props.getAccessKeySecret());
        log.info("OSS 客户端初始化完成，bucket={}", props.getBucket());
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.shutdown();
        }
    }

    public boolean isReady() {
        return client != null;
    }

    /**
     * 上传对象并返回公网访问 URL
     *
     * @param in         数据流（由调用方负责关闭）
     * @param size       字节数
     * @param contentType MIME 类型
     * @param objectKey  OSS 对象键，如 avatar/20261021/xxx.png
     */
    public String put(InputStream in, long size, String contentType, String objectKey) {
        if (client == null) {
            throw new BusinessException("OSS 未配置，无法上传图片");
        }
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(size);
        if (StringUtils.hasText(contentType)) {
            meta.setContentType(contentType);
        }
        try {
            client.putObject(props.getBucket(), objectKey, in, meta);
        } catch (Exception e) {
            log.error("OSS putObject 失败 bucket={} key={}", props.getBucket(), objectKey, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR.getCode(), "图片上传失败，请稍后重试");
        }
        String url = trimRight(props.getUrlPrefix()) + "/" + objectKey;
        log.info("OSS 上传成功: {}", url);
        return url;
    }

    private String trimRight(String s) {
        if (!StringUtils.hasText(s)) {
            return "";
        }
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}
