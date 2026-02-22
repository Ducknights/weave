package org.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private LocalDateTime birthday;
    private String address;
    private String motto;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
