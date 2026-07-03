package com.weave.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.weave.auth.model.CustomUserDetails;
import com.weave.auth.model.UserAuth;

@Mapper
public interface AuthMapper extends BaseMapper<UserAuth> {

    @Select("SELECT id,email,password FROM users WHERE email = #{email}")
    // 根据邮箱查询用户信息
    UserAuth selectUserByEmail(String email);

    @Insert("INSERT INTO user_roles(user_id,role_id) VALUES(#{id},2)")
    // 默认角色为普通用户
    void insertUserRole(Long id);

    // 一次查询获取所有信息，使用 GROUP_CONCAT 避免 N+1 问题
    @Select("SELECT " +
            "    u.id as userId, " +
            "    u.email as username, " +
            "    u.password, " +
            "    GROUP_CONCAT(DISTINCT r.name) as rolesStr, " +
            "    GROUP_CONCAT(DISTINCT p.name) as authoritiesStr " +
            "FROM users u " +
            "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
            "LEFT JOIN roles r ON ur.role_id = r.id " +
            "LEFT JOIN role_permissions rp ON r.id = rp.role_id " +
            "LEFT JOIN permissions p ON rp.permission_id = p.id " +
            "WHERE u.email = #{email} " +
            "GROUP BY u.id, u.email, u.password")
    CustomUserDetails selectUserDetailsByEmail(String email);

    @Select("SELECT " +
            "    u.id as userId, " +
            "    u.email as username, " +
            "    u.password, " +
            "    GROUP_CONCAT(DISTINCT r.name) as rolesStr, " +
            "    GROUP_CONCAT(DISTINCT p.name) as authoritiesStr " +
            "FROM users u " +
            "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
            "LEFT JOIN roles r ON ur.role_id = r.id " +
            "LEFT JOIN role_permissions rp ON r.id = rp.role_id " +
            "LEFT JOIN permissions p ON rp.permission_id = p.id " +
            "WHERE u.id = #{userId} " +
            "GROUP BY u.id, u.email, u.password")
    CustomUserDetails selectUserDetailsById(Long userId);
}
