package org.example.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterPart2Dto {
    @Email(message = "邮箱格式错误")
    private String email;
    @Size(min = 6, message = "密码长度不能小于6")
    private String password;
    @Size(min = 6, message = "验证码长度不能小于6")
    private String code;
}
