package com.kaola.controller;

import com.kaola.exception.UnsupportedFileTypeException;
import com.kaola.pojo.Result;
import com.kaola.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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


}
