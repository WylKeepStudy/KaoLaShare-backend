package com.kaola.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id; // 主键ID（自增）
    private String username;
    private String password;//加密后的密码
    private String avatarUrl; //头像URL
    private Date createTime;//注册时间
}
