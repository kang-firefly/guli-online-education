package com.guli.service.impl;

import com.guli.common.BusinessException;
import com.guli.common.ResultCode;
import com.guli.service.FileService;
import com.guli.util.OssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

/**
 * 文件服务实现：所有校验与 objectKey 规则都归业务层，OssUtil 只负责把字节传上去
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
    private static final long MAX_SIZE = 5 * 1024 * 1024L;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final OssUtil ossUtil;

    @Override
    public String uploadImage(MultipartFile file, String dir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片不能超过 5MB");
        }

        String ext = resolveExt(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("只支持 jpg/jpeg/png/gif/webp/bmp 格式");
        }

        String objectKey = buildObjectKey(dir, ext);
        try (InputStream in = file.getInputStream()) {
            return ossUtil.put(in, file.getSize(), file.getContentType(), objectKey);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("图片上传失败 objectKey={}", objectKey, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR.getCode(), "图片上传失败，请稍后重试");
        }
    }

    /** 取扩展名（小写）；无扩展名返回空串 */
    private String resolveExt(String originalFilename) {
        if (!StringUtils.hasText(originalFilename) || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }

    /** 目录只保留字母，防止路径穿越；文件名用 UUID 避免冲突与信息泄露 */
    private String buildObjectKey(String dir, String ext) {
        String safeDir = StringUtils.hasText(dir) ? dir.replaceAll("[^a-zA-Z]", "") : "common";
        return safeDir + "/" + LocalDate.now().format(DATE_FMT) + "/"
                + UUID.randomUUID().toString().replace("-", "") + "." + ext;
    }
}
