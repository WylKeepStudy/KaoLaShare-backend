package com.kaola.exception;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/23 14:12
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
