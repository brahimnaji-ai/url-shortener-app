package com.brahim.url.user;

public record registerRequest(
    String username,
    String email,
    String password
) {
}
