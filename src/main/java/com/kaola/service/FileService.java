package com.kaola.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/24 00:16
 */
public interface FileService {

    public Map<String, Object> downloadFile(Long fileId);

    Long uploadMaterial(MultipartFile file, String title, Long departmentId, Long currentUserId);
}
