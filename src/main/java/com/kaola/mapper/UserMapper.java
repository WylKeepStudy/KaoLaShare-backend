package com.kaola.mapper;


import com.kaola.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    // 1. 根据用户名查询用户（判断是否已存在）
    User findByUsername(@Param("username") String username);

    // 2. 插入新用户到数据库
    void insert(User user);

    // 3. 根据用户名查询用户（登录使用）
    @Select("SELECT id, username, password FROM t_user WHERE username = #{username}")
    User findByUsernameForLogin(@Param("username") String username);


    /**
     * 根据用户ID查询用户详细信息
     * 注意：不要查询密码字段返回给前端！
     * @param id 用户ID
     * @return 用户实体对象，不包含密码
     */
    @Select("SELECT id, username, avatar_url, create_time FROM t_user WHERE id = #{id}")
    User findById(@Param("id") Long id);


}