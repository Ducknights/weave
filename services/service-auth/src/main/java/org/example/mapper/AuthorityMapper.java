package org.example.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuthorityMapper {
    @Select("SELECT p.permission_name\n" +
            "FROM user u\n" +
            "         JOIN `user-role` ur ON u.id = ur.user_id\n" +
            "         JOIN `role-permission` rp ON ur.role_id = rp.role_id\n" +
            "         JOIN permission p ON rp.permission_id = p.permission_id\n" +
            "WHERE u.id = #{id}")
    List<String> selectUserPermissionById(@Param("id") Integer id);
}
