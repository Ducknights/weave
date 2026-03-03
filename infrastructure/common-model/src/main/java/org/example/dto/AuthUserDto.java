package org.example.dto;

public record AuthUserDto(
        Long id,
        String email,
        String password) {
}
