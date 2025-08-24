package com.kaola.service;


import com.kaola.pojo.LoginRequest;
import com.kaola.pojo.LoginResponse;
import com.kaola.pojo.Result;
import com.kaola.pojo.UserRegisterDTO;

public interface UserService {
    Result register(UserRegisterDTO registerDTO);

    LoginResponse login(LoginRequest request);
}
