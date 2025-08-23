package com.kaola.service.impl;

import com.kaola.exception.BusinessException;
import com.kaola.exception.FileNotFoundException;
import com.kaola.mapper.FileMapper;
import com.kaola.pojo.File;
import com.kaola.service.FileService;
import com.kaola.utils.AliyunOSSOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/24 00:18
 */

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;


/**
 * 下载文件业务逻辑
 * @param fileId 文件ID
 * @return 包含文件信息和文件流的Map
 * @throws FileNotFoundException 如果文件不存在
 * @throws Exception 如果从OSS下载失败
 */
    @Override
    public Map<String, Object> downloadFile(Long fileId) {
        // 1. 查询文件元数据
        File file = fileMapper.findById(fileId);
        if (file == null) {
            throw new FileNotFoundException("文件不存在或已被删除");
        }

        // 2. 更新下载次数
        fileMapper.incrementDownloadCount(fileId);

        // 3. 从阿里云OSS获取文件输入流
        // 需要在AliyunOSSOperator中添加一个download方法
        InputStream fileStream = aliyunOSSOperator.download(file.getFileUrl());
        if (fileStream == null) {
            throw new RuntimeException("未能从OSS获取文件流");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("fileName", file.getFileName());
        result.put("fileType", file.getFileType()); // 例如 "pdf", "docx"
        result.put("fileStream", fileStream); // 将InputStream传递给Controller
        return result;
    }


    /**
     * 上传学习资料文件业务逻辑
     * @param multipartFile 用户上传的文件
     * @param title 文件标题
     * @param departmentId 文件所属系ID
     * @param userId 上传用户ID
     * @return 新生成的文件ID
     * @throws BusinessException 如果文件上传或数据保存失败
     */
    public Long uploadMaterial(MultipartFile multipartFile, String title, Long departmentId, Long userId) {
        // 1. 提取文件类型
        String originalFilename = multipartFile.getOriginalFilename();
        String fileType = "unknown"; // 默认文件类型
        if (originalFilename != null && originalFilename.contains(".")) {
            fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        String fileUrl;
        try {
            // 2. 调用OSS工具类上传文件到files目录
            fileUrl = aliyunOSSOperator.upload(multipartFile.getBytes(), originalFilename, AliyunOSSOperator.FileCategory.FILE);
        } catch (IOException e) {
            throw new BusinessException("文件读取失败", e);
        } catch (Exception e) {
            throw new BusinessException("文件上传到OSS失败", e);
        }

        // 3. 封装File实体
        File newFile = new File();
        newFile.setUserId(userId);
        newFile.setDepartmentId(departmentId);
        newFile.setFileName(title);
        newFile.setFileUrl(fileUrl);
        newFile.setFileType(fileType);
        newFile.setDownloadCount(0); // 初始下载次数为0
        newFile.setCreateTime(LocalDateTime.now()); // 设置当前上传时间

        // 4. 调用Mapper层保存文件元数据
        try {
            fileMapper.insertFile(newFile);
        } catch (Exception e) {
            // 记录日志，并抛出业务异常
            throw new BusinessException("文件元数据保存失败", e);
        }

        return newFile.getId(); // 返回新生成的文件ID
    }

}
