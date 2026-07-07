package com.brahim.shortener.user.dto.response;

public record LoginResponse(
   String accessToken,
   String refreshToken,
   long expiresIn,
   String tokenType
) {}
