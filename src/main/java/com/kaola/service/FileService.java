package com.kaola.service;

import com.kaola.pojo.File;
import com.kaola.pojo.FileDownloadInfoDTO;
import com.kaola.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/24 00:16
 */
public interface FileService {

    public FileDownloadInfoDTO downloadFile(Long fileId) throws Exception;

    Long uploadMaterial(MultipartFile file, String title, Long departmentId, Long currentUserId);

    PageResult getFileList(Integer pageNum, Integer pageSize, Long departmentId, String keyword);
}
