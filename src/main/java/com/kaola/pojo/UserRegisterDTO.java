package com.kaola.pojo;


import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username; // 前端输入的用户名
    private String password; // 前端输入的明文密码
    private String avatarUrl; // 前端可选的头像URL
}
