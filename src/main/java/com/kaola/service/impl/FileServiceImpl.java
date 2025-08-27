package com.kaola.service.impl;

import com.aliyun.oss.model.OSSObject;
import com.kaola.exception.BusinessException;
import com.kaola.exception.FileNotFoundException;
import com.kaola.mapper.FileMapper;
import com.kaola.pojo.File;
import com.kaola.pojo.FileDownloadInfoDTO;
import com.kaola.service.FileService;
import com.kaola.utils.AliyunOSSOperator;
import com.kaola.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
     * @return 包含文件信息和OSSObject的FileDownloadInfo对象
     * @throws FileNotFoundException 如果文件不存在
     * @throws BusinessException 如果从OSS下载失败
     */
    public FileDownloadInfoDTO downloadFile(Long fileId) {
        // 1. 查询文件元数据
        File file = fileMapper.findById(fileId);
        if (file == null) {
            throw new FileNotFoundException("文件不存在或已被删除");
        }

        // 2. 更新下载次数
        fileMapper.incrementDownloadCount(fileId);

        OSSObject ossObject;
        try {
            // 3. 从阿里云OSS获取OSSObject
            ossObject = aliyunOSSOperator.download(file.getFileUrl());
            if (ossObject == null || ossObject.getObjectContent() == null) {
                throw new BusinessException("未能从OSS获取文件流");
            }
        } catch (Exception e) {
            throw new BusinessException("从OSS下载文件失败: " + e.getMessage(), e);
        }

        // 4. 封装并返回FileDownloadInfo
        String fileExtension = "";
        int lastDotIndex = file.getFileName().lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < file.getFileName().length() - 1) {
            fileExtension = file.getFileName().substring(lastDotIndex + 1).toLowerCase(); // 获取不带点的后缀
        }

        return new FileDownloadInfoDTO(
                file.getFileName(),
                fileExtension, // 传递文件后缀，用于Controller判断ContentType
                ossObject.getObjectMetadata().getContentLength(), // 获取文件内容长度
                ossObject // 将OSSObject传递给Controller
        );
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
        newFile.setFileName(originalFilename);
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




    /**
     * 获取文件列表业务逻辑
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param departmentId 文件所属系ID
     * @param keyword 文件名称关键词
     * @return 包含文件列表的分页结果
     */
    @Override
    public PageResult getFileList(Integer pageNum, Integer pageSize, Long departmentId, String keyword) {
        // 1. 计算分页起始位置：(页码-1)*每页条数（MySQL LIMIT语法需要）
        Integer startIndex = (pageNum - 1) * pageSize;

        // 2. 调用Mapper查询：当前页数据 + 总条数
        List<File> fileList = fileMapper.selectFileList(startIndex, pageSize,
                departmentId, keyword);
        Long total = fileMapper.selectFileTotal(departmentId, keyword);

        // 3. 封装分页结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(fileList);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);

        return pageResult;
    }

}
