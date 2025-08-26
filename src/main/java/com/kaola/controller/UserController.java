package com.kaola.controller;


import com.kaola.exception.BusinessException;
import com.kaola.pojo.*;
import com.kaola.service.UserService;
import com.kaola.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
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



    /**
     * 登录
     * @param request 登录参数
     * @return 登录结果
     */
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



    /**
     * 获取当前登录用户的详细信息
     * 路径：/user/info
     * 需要JWT Token认证
     * @return 用户的详细信息
     */
    @GetMapping("/info")
    public Result getUserInfo() {
        // 1. 从 UserContext 中获取当前登录用户的ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            // 理论上，如果请求通过了TokenInterceptor，这里不应该是null
            // 但作为防御性编程，可以处理一下，或者直接抛出认证异常
            log.error("尝试获取用户信息时，UserContext中未找到用户ID。");
            return Result.error(401, "用户未登录或登录信息失效");
        }

        log.info("获取用户ID: {} 的信息", userId);
        try {
            // 2. 调用Service层获取用户详细信息 (Service现在直接返回UserInfoDTO)
            UserInfoDTO userInfoDTO = userService.getUserInfo(userId); // 接收 UserInfoDTO

            // 3. 封装响应数据 (只返回必要信息，不包括密码)
            // 可以创建一个专门的 UserInfoDTO，或者在这里手动构建Map
            // 为了简化，这里直接返回User对象，但请确保UserMapper查询时已排除密码
            return Result.success(userInfoDTO);
        } catch (BusinessException e) {
            // 捕获Service层抛出的业务异常，例如用户不存在
            log.error("获取用户信息业务处理失败: {}", e.getMessage());
            return Result.error(400, e.getMessage()); // 业务异常通常返回400
        } catch (Exception e) {
            log.error("获取用户信息失败，发生未知错误: ", e);
            return Result.error(500, "获取用户信息失败，服务器内部错误");
        }
    }



}
