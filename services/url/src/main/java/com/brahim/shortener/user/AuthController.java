package com.brahim.shortener.user;


import com.brahim.shortener.user.dto.request.LoginRequest;
import com.brahim.shortener.user.dto.request.RefreshRequest;
import com.brahim.shortener.user.dto.request.RegisterRequest;
import com.brahim.shortener.user.dto.response.LoginResponse;
import com.brahim.shortener.user.dto.response.RefreshResponse;
import com.brahim.shortener.user.dto.response.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest request
    ){
        return ResponseEntity.status(CREATED).body(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request
    ){
        return ResponseEntity.ok(service.login(request));
    }


    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @RequestBody @Valid RefreshRequest request
    ){
        return ResponseEntity.ok(service.refresh(request));
    }


}
