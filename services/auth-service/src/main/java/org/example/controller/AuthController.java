package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.example.dto.ApiRequestDto;
import org.example.model.AuthApiResponse;
import org.example.dto.VerifyCodeDto;
import org.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthApiResponse<?>> login(@Valid @NotNull @RequestBody ApiRequestDto apiRequestDto) {
        final AuthApiResponse<?> response = authService.login(apiRequestDto);
        return ResponseEntity.status(response.code()).body(response);
    }

    @PostMapping("/register/sendCode")
    public ResponseEntity<AuthApiResponse<?>> sendCode(@Valid @NotNull @RequestBody ApiRequestDto apiRequestDto) {
        final AuthApiResponse<?> response = authService.sendCode(apiRequestDto);
        return ResponseEntity.status(response.code()).body(response);
    }

    @PostMapping("/register/verifyCode")
    public ResponseEntity<AuthApiResponse<?>> verify(@Valid @NotNull @RequestBody VerifyCodeDto dto) {
        final AuthApiResponse<?> response = authService.verifyCode(dto);
        return ResponseEntity.status(response.code()).body(response);
    }

    @PostMapping("/logout")
    public AuthApiResponse<?> logout() {
        Long userId = 17L;
        return authService.logout(userId);
    }

    @PostMapping("/access")
    public ResponseEntity<AuthApiResponse<?>> getNewToken() {
        Long userId = 17L;
        final AuthApiResponse<?> response = authService.getNewSuccessToken(userId);
        return ResponseEntity.status(response.code()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthApiResponse<?>> getNewRefreshToken() {
        Long userId = 17L;
        final AuthApiResponse<?> response = authService.getNewRefreshToken(userId);
        return ResponseEntity.status(response.code()).body(response);
    }
}
