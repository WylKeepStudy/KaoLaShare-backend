package com.kaola.utils;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * JWT 工具类
 * 用于生成和解析JWT令牌
 */
@Component
public class JwtUtil {

    // JWT 密钥，"kaolashare" 的 Base64 编码
    private static final String SECRET_KEY = "a2FvbGFzaGFyZQ==";
    // 令牌的过期时间，设置为24小时（单位：毫秒）
    private static final Long EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24小时 * 60分钟 * 60秒 * 1000毫秒

    /**
     * 生成 JWT 令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 生成的 JWT 字符串
     */
    public String generateToken(Long userId, String username) {
        // 设置JWT的Claims（声明），即有效载荷
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        // 设置过期时间
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)           // 设置自定义Claims
                .setExpiration(expiration)   // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 使用HS256算法和预设密钥进行签名
                .compact();                  // 构建并压缩成JWT字符串
    }

    /**
     * 解析 JWT 令牌
     *
     * @param token JWT令牌字符串
     * @return Claims 对象，包含令牌中的所有声明（如 userId, username, exp 等）
     * @throws JwtException 如果令牌无效或过期
     */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)   // 设置用于验证签名的预设密钥
                .parseClaimsJws(token)       // 解析JWT令牌
                .getBody();                  // 获取Claims主体
    }
}
