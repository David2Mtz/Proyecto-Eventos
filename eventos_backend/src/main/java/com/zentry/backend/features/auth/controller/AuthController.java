package com.zentry.backend.features.auth.controller;

import com.zentry.backend.features.auth.dto.LoginRequest;
import com.zentry.backend.features.auth.dto.LoginResponse;
import com.zentry.backend.features.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}