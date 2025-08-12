package org.example.controller;

import jakarta.annotation.Resource;
import org.example.model.ApiResponse;
import org.example.model.ApiRequest;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody ApiRequest apiRequest) {
        if (authService.login(apiRequest).getCode() == 200) {
            return ResponseEntity.ok(authService.login(apiRequest));
        }else if (authService.login(apiRequest).getCode() == 401) {
            return ResponseEntity.status(401).body(authService.login(apiRequest));
        }else{
            return ResponseEntity.status(500).body(authService.login(apiRequest));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody ApiRequest apiRequest) {
        if (authService.signup(apiRequest).getCode() == 200) {
            return ResponseEntity.ok(authService.signup(apiRequest));
        }else if(authService.signup(apiRequest).getCode() == 409) {
            return ResponseEntity.status(409).body(authService.signup(apiRequest));
        }else {
            return ResponseEntity.status(500).body(authService.signup(apiRequest));
        }
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        // 调用服务层进行登出
        return authService.logout();
    }
}
