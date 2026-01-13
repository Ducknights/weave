package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.example.model.ApiRequest;
import org.example.model.AuthApiResponse;
import org.example.model.RegisterPart2Dto;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
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

    @PostMapping("/register/part1")
    public ResponseEntity<AuthApiResponse<?>> sendCode(@Valid @NotNull @RequestBody ApiRequest apiRequest) {
        final AuthApiResponse<?> response = authService.signup(apiRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/register/part2")
    public ResponseEntity<AuthApiResponse<?>> verify(@Valid @NotNull @RequestBody RegisterPart2Dto dto) {
        final AuthApiResponse<?> response = authService.register(dto);
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
