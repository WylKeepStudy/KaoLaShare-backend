package com.kaola.controller;


import com.kaola.pojo.*;
import com.kaola.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 注册
     * @param registerDTO 注册参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            LoginResponse loginData = userService.login(request);
            response.put("code", 200);
            response.put("msg", "登录成功");
            response.put("data", loginData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("code", 401);
            response.put("msg", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
