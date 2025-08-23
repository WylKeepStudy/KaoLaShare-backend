package com.kaola.controller;

import com.kaola.exception.BusinessException;
import com.kaola.exception.UnsupportedFileTypeException;
import com.kaola.pojo.Result;
import com.kaola.service.FileService;
import com.kaola.utils.AliyunOSSOperator;
import com.kaola.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/18 21:17
 */

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;


    /**
     * 上传用户头像文件
     * @param file 文件
     * @return 上传结果 url
     * @throws Exception 上传过程中可能抛出的异常
     */
    @PostMapping("/upload/avatar")
    public Result uploadAvatar(MultipartFile file) throws Exception {
        log.info("上传头像: {}",  file.getOriginalFilename());

        // --- 校验文件类型是否为图片 ---
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png") && !contentType.startsWith("image/jpg"))) {
            // 抛出自定义异常，由全局异常处理器捕获
            throw new UnsupportedFileTypeException("文件类型不支持，仅支持jpg,jpeg,png格式！");
        }
        // --- 校验结束 ---

        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename(), AliyunOSSOperator.FileCategory.IMAGE);
        log.info("上传成功: {}", url);
        return Result.success(url);
    }


    /**
     * 上传学习资料文件接口
     * 路径：/file/upload
     * 接收用户上传的文件、标题、所属系ID，并保存元数据
     *
     * @param file 用户上传的文件
     * @param title 文件标题
     * @param departmentId 文件所属系ID
     * @return 包含新生成文件ID的成功响应
     */
    @PostMapping("/upload")
    public Result uploadMaterial(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("departmentId") Long departmentId) {

        log.info("开始上传文件：文件名={}, 标题={}, 所属系ID={}", file.getOriginalFilename(), title, departmentId);

        // 1. 基本参数校验
        if (file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }
        if (title == null || title.trim().isEmpty()) {
            return Result.error(400, "文件标题不能为空");
        }
        if (departmentId == null) {
            return Result.error(400, "请选择文件所属系");
        }

        // 从JWT Token中获取当前登录用户的ID
        Long currentUserId = UserContext.getUserId();

        try {
            // 2. 调用Service层处理文件上传和元数据保存
            Long newFileId = fileService.uploadMaterial(file, title, departmentId, currentUserId);
            log.info("文件上传成功，新文件ID: {}", newFileId);
            return Result.success(newFileId); // 返回新文件ID
        } catch (BusinessException e) {
            // 捕获Service层抛出的业务异常
            log.warn("文件上传业务处理失败: {}", e.getMessage());
            return Result.error(400, e.getMessage()); // 业务异常通常返回400
        } catch (Exception e) {
            // 捕获其他未知异常
            log.error("文件上传失败，发生未知错误: ", e);
            return Result.error(500, "文件上传失败，服务器内部错误");
        }
    }




}
