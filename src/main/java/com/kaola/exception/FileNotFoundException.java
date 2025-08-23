package com.kaola.exception;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/24 00:23
 */
public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
    }
}