package com.kaola.mapper;


import com.kaola.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    // 1. 根据用户名查询用户（判断是否已存在）
    User findByUsername(@Param("username") String username);

    // 2. 插入新用户到数据库
    void insert(User user);
}