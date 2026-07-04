package com.weave.user.service;

import com.weave.model.model.dto.AuthUserDto;
import com.weave.model.model.dto.UserBriefDto;
import com.weave.user.model.vo.UserInfoVo;
import com.weave.user.model.dto.UpdateUserInfoDto;
import com.weave.user.model.entity.UserInfo;

import java.util.Map;
import java.util.Set;

public interface UserInfoService {

    Map<Long, UserBriefDto> getUserInfosByIds(Set<Long> ids);

    UserInfo createUser(AuthUserDto user);

    UserBriefDto getUserBriefDtoById(Long id);

    UpdateUserInfoDto updateUser(UpdateUserInfoDto user);

    UserInfoVo getUserInfoDtoById(Long id);
}