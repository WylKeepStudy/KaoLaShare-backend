package com.kaola.exception;

/**
 * 自定义业务异常类
 * 用于Service层抛出，表示业务逻辑上的错误，通常前端会收到400状态码。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}