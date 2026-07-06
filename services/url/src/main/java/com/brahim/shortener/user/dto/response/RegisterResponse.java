package com.brahim.shortener.user.dto.response;

public record RegisterResponse(
    Long id,
    String username,
    String email,
    String message
) {}
