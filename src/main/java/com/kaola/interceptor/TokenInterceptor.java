package com.kaola.interceptor;

import com.kaola.utils.JwtUtil;
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
            JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.info("token解析失败, 拦截请求: {}", requestURI);
            response.setStatus(401);
            return false;
        }

        log.info("请求通过验证, 放行请求: {}", requestURI);
        return true;

    }
}
