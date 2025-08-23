package com.kaola.utils;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 存储当前请求的用户ID，实现线程隔离。
 */
public class UserContext {

    // ThreadLocal 变量，用于存储当前线程的用户ID
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    /**
     * 设置当前线程的用户ID
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }

    /**
     * 获取当前线程的用户ID
     * @return 用户ID
     */
    public static Long getUserId() {
        return userIdHolder.get();
    }

    /**
     * 清除当前线程的用户ID，防止内存泄露和数据污染
     * 在请求处理完成后（如拦截器的 afterCompletion 方法中）必须调用。
     */
    public static void clear() {
        userIdHolder.remove();
    }
}
