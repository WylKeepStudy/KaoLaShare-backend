package com.kaola.exception;

import com.kaola.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/22 20:15
 * 全局异常处理类
 */


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public Result handleException(Exception e) {
        log.error("服务器发生异常: ", e);
        return Result.error(500, "服务器发生异常");
    }

    /**
     * 处理自定义的 UnsupportedFileTypeException 异常
     * 返回码 400，表示客户端错误
     */
    @ExceptionHandler(UnsupportedFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 设置HTTP状态码为400 Bad Request
    public Result handleUnsupportedFileTypeException(UnsupportedFileTypeException e) {
        log.error("文件类型不支持: {}", e.getMessage());
        return Result.error(400, "文件类型不支持，仅支持jpg,jpeg,png格式！");
    }


    /**
     * 处理用户名已存在的业务异常
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 设置HTTP状态码为400 Bad Request
    public Result handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        log.error("用户名已存在: {}", e.getMessage());
        return Result.error(400, "用户名已存在!");

    }

}
