package org.example.service;

import org.example.dto.AuthUserDto;
import org.example.dto.UserBriefDto;
import org.example.model.entity.UserInfo;

import java.util.Map;
import java.util.Set;

public interface UserInfoService {

    Map<Long, UserBriefDto> getUserInfosByIds(Set<Long> ids);

    UserInfo createUser(AuthUserDto user);

    UserBriefDto getUserBriefDtoById(Long id);

    Boolean refresh(Long userId);

    UserInfo updateUser(UserInfo user);

    UserInfo getSelfInfo(Long id);
}