package org.example.dto;

import lombok.Data;

@Data
public class AuthUserDto {
    private Long id;
    private String email;
    private String password;
}
