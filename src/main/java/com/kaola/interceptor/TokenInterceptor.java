package com.kaola.interceptor;

import com.kaola.utils.JwtUtil;
import com.kaola.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/23 17:14
 */

@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI(); // 获取请求的URI eg: /user/login
        log.info("拦截请求: {}", requestURI);

        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            log.info("请求未携带token, 拦截请求: {}", requestURI);
            response.setStatus(401);
            return false;
        }

        try {
            Claims claims = JwtUtil.parseToken(token); // 解析JWT令牌
            Long userId = claims.get("userId", Long.class); // 从Claims中获取userId
            if (userId == null) {
                log.error("Token解析成功但未找到userId, 拦截请求: {}", requestURI);
                response.setStatus(401); // 无效的token内容
                return false;
            }
            // 将userId存入线程上下文，供后续业务逻辑使用
            UserContext.setUserId(userId);
        } catch (Exception e) {
            log.info("token解析失败, 拦截请求: {}", requestURI);
            response.setStatus(401);
            return false;
        }

        log.info("请求通过验证, 放行请求: {}", requestURI);
        return true;

    }


    /**
     * 请求处理完成后，清除 ThreadLocal 中的用户ID，防止内存泄露和数据污染
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
        log.debug("请求URI {} 处理完成，UserContext已清除", request.getRequestURI());
    }
}
