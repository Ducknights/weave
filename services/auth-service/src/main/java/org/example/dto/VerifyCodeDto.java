package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record VerifyCodeDto(
        @Email(message = "邮箱格式错误")
        String email,
        @Size(min = 6, message = "密码长度不能小于6")
        String password,
        @Size(min = 6, message = "验证码长度不能小于6")
        String code) {
}
