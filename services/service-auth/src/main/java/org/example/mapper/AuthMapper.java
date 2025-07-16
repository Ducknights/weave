package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.UserAuth;

@Mapper
public interface AuthMapper {

    @Select("SELECT id,email,password FROM user WHERE email = #{email}")
    UserAuth selectUserByEmail(String email);

    @Insert("INSERT INTO user(email,password,created_at) VALUES(#{email},#{password},CURRENT_TIMESTAMP)")
    void insertUser(UserAuth userAuth);
}
