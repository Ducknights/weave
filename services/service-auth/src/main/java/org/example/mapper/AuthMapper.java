package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.UserAuth;

import java.util.List;

@Mapper
public interface AuthMapper {

    @Select("SELECT id,email,password FROM user WHERE email = #{email}")
    UserAuth selectUserByEmail(String email);

    @Insert("INSERT INTO user(email,password,created_at) VALUES(#{email},#{password},CURRENT_TIMESTAMP)")
    // 插入一个用户信息，包括邮箱、密码和创建时间
    void insertUser(UserAuth userAuth);
}
