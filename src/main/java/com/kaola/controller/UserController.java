package com.kaola.controller;


import com.kaola.pojo.Result;
import com.kaola.pojo.UserRegisterDTO;
import com.kaola.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
