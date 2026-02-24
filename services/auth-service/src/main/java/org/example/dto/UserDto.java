package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
