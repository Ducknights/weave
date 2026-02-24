package org.example.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id, String name,
        String email,
        String gender,
        LocalDateTime birthday,
        String address,
        String motto,
        String avatar,
        LocalDateTime createTime,
        LocalDateTime updateTime) {
}
