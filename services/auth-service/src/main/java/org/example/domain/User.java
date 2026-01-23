package org.example.domain;

import jakarta.security.auth.message.AuthException;

public class User {

    private UserId id;
    private Email email;
    private Password password;

    public void login(String rawPassword, PasswordService passwordService) {
        if (!passwordService.matches(rawPassword, this.password)) {
            throw new AuthException("密码错误");
        }
    }
}
