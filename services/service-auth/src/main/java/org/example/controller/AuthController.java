package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.AuthResponse;
import org.example.dto.AuthRequest;
import org.example.service.AuthService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public AuthResponse<?> login(@RequestBody AuthRequest authRequest) {
        // 调用服务层进行认证
        return authService.login(authRequest);
    }

    @PostMapping("/signup")
    public AuthResponse<?> signup(@RequestBody AuthRequest authRequest) {
        // 调用服务层进行注册
        return authService.signup(authRequest);
    }

    @PostMapping("/logout")
    public AuthResponse<?> logout() {
        // 调用服务层进行登出
        return authService.logout();
    }
}
