package com.weave.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ApiRequestDto(
        @Email(message = "邮箱格式错误")
        String email,
        @Size(min = 6, message = "密码长度不能小于6")
        String password) {
}
