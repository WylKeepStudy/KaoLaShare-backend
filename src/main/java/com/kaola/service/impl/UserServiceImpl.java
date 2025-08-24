package com.kaola.service.impl;

import com.kaola.exception.UsernameAlreadyExistsException;
import com.kaola.mapper.UserMapper;
import com.kaola.pojo.*;
import com.kaola.service.UserService;
import com.kaola.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired // 自动注入Mapper
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private JwtUtil jwtUtils;
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

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsernameForLogin(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 生成 JWT 令牌（包含 userId 和 username）
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 4. 返回登录成功数据
        return new LoginResponse(token);
    }

}
