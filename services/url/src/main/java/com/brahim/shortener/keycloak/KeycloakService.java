package com.brahim.shortener.keycloak;

import com.brahim.shortener.user.dto.request.RegisterRequest;

public interface KeycloakService {
    String createUser(RegisterRequest request);
    boolean userExists(String username);
    boolean emailExists(String email);
}
