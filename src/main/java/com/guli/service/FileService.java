package com.guli.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务：图片上传（讲师头像 / 课程封面 / Banner）
 */
public interface FileService {

    /**
     * 校验并上传图片
     *
     * @param file 前端上传的文件
     * @param dir  业务目录：avatar / cover / banner
     * @return 可公开访问的 URL
     */
    String uploadImage(MultipartFile file, String dir);
}
