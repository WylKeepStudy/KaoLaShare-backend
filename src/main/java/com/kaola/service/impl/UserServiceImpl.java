package com.kaola.service.impl;

import com.kaola.exception.UsernameAlreadyExistsException;
import com.kaola.mapper.UserMapper;
import com.kaola.pojo.Result;
import com.kaola.pojo.User;
import com.kaola.pojo.UserRegisterDTO;
import com.kaola.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired // 自动注入Mapper
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result register(UserRegisterDTO registerDTO) {

        User existingUser = userMapper.findByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new UsernameAlreadyExistsException("用户名已存在");
        }

        // 使用BCrypt加密密码（核心安全步骤）
        String encryptedPassword = passwordEncoder.encode(registerDTO.getPassword());

        //封装用户数据
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encryptedPassword); // 存储加密后的密码
        if (registerDTO.getAvatarUrl() != null && !registerDTO.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(registerDTO.getAvatarUrl()); // 头像可为空
        } else {
            user.setAvatarUrl("null");
        }
        user.setCreateTime(new Date()); // 注册时间设为当前时间

        userMapper.insert(user);
        return Result.success("注册成功");

    }
}
