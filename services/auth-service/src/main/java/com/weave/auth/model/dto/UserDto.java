package com.weave.auth.model.dto;

import lombok.Getter;
import lombok.Setter;
import com.weave.model.model.dto.UserBriefDto;

import java.util.List;

@Getter
@Setter
public class UserDto extends UserBriefDto {
    private List<String> roles;

    public UserDto(Long userId, String name, String avatar, List<String> roleNames) {
        super(userId, name, avatar);
        this.roles = roleNames;
    }
}
