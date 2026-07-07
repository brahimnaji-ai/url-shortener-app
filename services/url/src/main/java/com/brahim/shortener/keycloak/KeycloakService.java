package com.brahim.shortener.keycloak;

import com.brahim.shortener.user.dto.request.RegisterRequest;
import com.brahim.shortener.user.dto.response.LoginResponse;
import com.brahim.shortener.user.dto.response.RefreshResponse;

import java.io.IOException;

public interface KeycloakService {
    String createUser(RegisterRequest request);
    boolean userExists(String username);
    boolean emailExists(String email);

    LoginResponse login(String username, String password);

    RefreshResponse refresh(String refreshToken) throws IOException, InterruptedException;
}
