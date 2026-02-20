package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.dto.UserDto;
import org.example.entity.UserAuth;

@Mapper
public interface AuthMapper {

    @Select("SELECT id,email,password FROM users WHERE email = #{email}")
    // 根据邮箱查询用户信息
    UserAuth selectUserByEmail(String email);

    @Insert("INSERT INTO users(email,password) VALUES(#{email},#{password})")
    // 插入一个用户信息，包括邮箱、密码和创建时间
    UserAuth insertUser(UserAuth userAuth);

    @Select("SELECT id,avatar,name,motto FROM users WHERE id = #{id}")
    // 根据邮箱和密码查询用户信息
    UserDto selectUserInfo(int id);
}