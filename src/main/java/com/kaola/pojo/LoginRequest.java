package com.kaola.pojo;

public class LoginRequest {
    private String username; // 前端传入的用户名
    private String password; // 前端传入的明文密码

    // Getter 和 Setter 方法
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
