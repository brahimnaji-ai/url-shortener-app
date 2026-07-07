package com.brahim.shortener.user.dto.response;

public record RefreshResponse(
   String accessToken,
   String refreshToken
) {}
