package com.brahim.url.user;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    public registerResponse register(@Valid registerRequest request) {
    }
}
