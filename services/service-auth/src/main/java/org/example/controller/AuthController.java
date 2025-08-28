package org.example.controller;

import jakarta.annotation.Resource;
import org.example.model.ApiRequest;
import org.example.model.AuthApiResponse;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthApiResponse<?>> login(@RequestBody ApiRequest apiRequest) {
        System.out.println("apiRequest:" + apiRequest);
        final AuthApiResponse<?> response = authService.login(apiRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthApiResponse<?>> signup(@RequestBody ApiRequest apiRequest) {
        final AuthApiResponse<?> response = authService.signup(apiRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/logout")
    public AuthApiResponse<?> logout() {
        System.out.println("logout");
        // 调用服务层进行登出
        return authService.logout();
    }

    @GetMapping("/test")
    public ResponseEntity<AuthApiResponse<?>> getNewToken() {
        System.out.println("userId:");
        return ResponseEntity.ok(new AuthApiResponse<>(100, "成功", null));
    }
}
