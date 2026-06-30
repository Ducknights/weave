package org.example.service;

import org.example.dto.AuthUserDto;
import org.example.dto.UserBriefDto;
import org.example.model.dto.UpdateUserInfoDto;
import org.example.model.entity.UserInfo;
import org.example.model.vo.UserInfoVo;

import java.util.Map;
import java.util.Set;

public interface UserInfoService {

    Map<Long, UserBriefDto> getUserInfosByIds(Set<Long> ids);

    UserInfo createUser(AuthUserDto user);

    UserBriefDto getUserBriefDtoById(Long id);

    UpdateUserInfoDto updateUser(UpdateUserInfoDto user);

    UserInfoVo getUserInfoDtoById(Long id);
}