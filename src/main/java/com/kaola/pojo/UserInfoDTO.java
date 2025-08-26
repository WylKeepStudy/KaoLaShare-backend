package com.kaola.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息数据传输对象 (DTO)
 * 用于封装获取用户信息接口的响应数据，避免直接暴露数据库实体。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long userId;        // 用户ID
    private String username;    // 用户名
    private String avatarUrl;   // 用户头像URL
}
