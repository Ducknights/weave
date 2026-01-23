package org.example.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.util.ValidationUtil;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequest {
    @Email(message = "邮箱格式错误")
    private String email;
    @Size(min = 6, message = "密码长度不能小于6")
    private String password;
}
