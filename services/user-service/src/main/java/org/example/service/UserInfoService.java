package org.example.service;

import org.example.dto.AuthUserDto;
import org.example.entity.UserInfo;

import java.util.Map;
import java.util.Set;

public interface UserInfoService {

    Map<Long, UserInfo> getUserInfosByIds(Set<Long> ids);

    UserInfo createUser(AuthUserDto user);

    UserInfo getUserById(Long id);

    Boolean refresh(Long userId);

    UserInfo updateUser(UserInfo user);
}