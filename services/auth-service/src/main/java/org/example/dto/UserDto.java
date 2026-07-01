package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.model.dto.UserBriefDto;

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
