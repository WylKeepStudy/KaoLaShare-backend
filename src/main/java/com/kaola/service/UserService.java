package com.kaola.service;


import com.kaola.pojo.*;

public interface UserService {
    Result register(UserRegisterDTO registerDTO);

    LoginResponse login(LoginRequest request);

    UserInfoDTO getUserInfo(Long userId);
}
