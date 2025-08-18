package com.kaola.controller;

import com.kaola.pojo.Result;
import com.kaola.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
public class FileController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;


    /**
     * 上传用户头像文件
     * @param file 文件
     * @return 上传结果 url
     * @throws Exception 上传过程中可能抛出的异常
     */
    @PostMapping("/file/upload/avatar")
    public Result uploadAvatar(MultipartFile file) throws Exception {
        log.info("上传头像: {}",  file.getOriginalFilename());
        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename(), AliyunOSSOperator.FileCategory.IMAGE);
        log.info("上传成功: {}", url);
        return Result.success(url);
    }


}
