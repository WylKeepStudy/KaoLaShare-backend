package com.kaola;

import com.kaola.utils.JwtUtil;
import io.jsonwebtoken.Claims;

/**
 * @version 1.0
 * @Author wyl
 * @Date 2025/8/24 01:39
 */
public class JwtTest {

    public static void main(String[] args) {
        String token = JwtUtil.generateToken(1L, "testuser");
        System.out.println(token);
        Claims claims = JwtUtil.parseToken(token);
        System.out.println(claims.get("username"));
        System.out.println(claims.get("userId"));
    }
}
