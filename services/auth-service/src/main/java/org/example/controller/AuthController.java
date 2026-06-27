package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ApiRequestDto;
import org.example.model.ApiResult;
import org.example.dto.VerifyCodeDto;
import org.example.model.AuthApiStatus;
import org.example.service.AuthService;
import org.example.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResult<?>> login(@Valid @NotNull @RequestBody ApiRequestDto apiRequestDto) {
        log.info("login: {}", apiRequestDto);
        var apiResult = authService.login(apiRequestDto);
        return ResponseEntity.ok()
                .body(AuthApiStatus.LOGIN_SUCCESS.response(apiResult));
    }

    @PostMapping("/register/code")
    public ResponseEntity<ApiResult<?>> sendCode(@Valid @NotNull @RequestBody ApiRequestDto apiRequestDto) {
        authService.sendCode(apiRequestDto);
        return ResponseEntity.ok()
                .body(AuthApiStatus.CODE_SEND_SUCCESS.response());
    }

    @PostMapping("/register/code/verify")
    public ResponseEntity<ApiResult<?>> verify(@Valid @NotNull @RequestBody VerifyCodeDto dto) {
        log.info("verify: {}", dto);
        authService.verifyCode(dto);
        return ResponseEntity.status(201)
                .body(AuthApiStatus.REGISTER_SUCCESS.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<?>> logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.logout(userId);
        return ResponseEntity.ok()
                .body(AuthApiStatus.LOGOUT_SUCCESS.response());
    }

    @GetMapping("/access")
    public ResponseEntity<ApiResult<?>> getNewToken(@RequestHeader(org.example.constant.RequestHeader.X_USER_ID) String userId) {
        log.info("getNewToken: {}", userId);
        var token = authService.getNewSuccessToken(Long.valueOf(userId));
        return ResponseEntity.ok()
                .body(AuthApiStatus.NEW_TOKEN_SUCCESS.response(token));
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResult<?>> getNewRefreshToken(@RequestHeader(org.example.constant.RequestHeader.X_USER_ID) String userId) {
        var token = authService.getNewRefreshToken(Long.valueOf(userId));
        return ResponseEntity.ok()
                .body(AuthApiStatus.NEW_TOKEN_SUCCESS.response(token));
    }
}
