package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<AuthApiResponse<?>> login(@Valid @NotNull @RequestBody ApiRequest apiRequest) {
        final AuthApiResponse<?> response = authService.login(apiRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthApiResponse<?>> signup(@Valid @NotNull @RequestBody ApiRequest apiRequest) {
        final AuthApiResponse<?> response = authService.signup(apiRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/logout")
    public AuthApiResponse<?> logout() {
        return authService.logout();
    }

    @PostMapping("/access")
    public ResponseEntity<AuthApiResponse<?>> getNewToken(@RequestHeader("X-UserId") String userId) {
        final AuthApiResponse<?> response = authService.getNewSuccessToken(userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthApiResponse<?>> getNewRefreshToken(@RequestHeader("X-UserId") String userId) {
        final AuthApiResponse<?> response = authService.getNewRefreshToken(userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
