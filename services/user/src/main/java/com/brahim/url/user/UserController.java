package com.brahim.url.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<registerResponse> register(
            @RequestBody @Valid registerRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<> login(
            @RequestBody @Valid loginRequest request
    ){
        re
    }
    @GetMapping("/me")
    public ResponseEntity<> getCurrentUser(){

    }
}
