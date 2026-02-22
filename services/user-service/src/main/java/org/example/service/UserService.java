package org.example.service;

import org.example.dto.AuthUserDto;
import org.example.entity.UserInfo;

import java.util.Map;
import java.util.Set;

public interface UserService {
    /**
     * 根据用户ID列表批量获取用户信息
     * @param userIds 用户ID集合
     * @return 用户信息映射，key为用户ID，value为用户信息
     */
    Map<Long, UserInfo> getUserInfosByIds(Set<Long> userIds);

    UserInfo createUser(AuthUserDto user);

    UserInfo getUserById(Long id);
}