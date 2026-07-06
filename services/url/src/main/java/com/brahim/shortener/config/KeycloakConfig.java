package com.brahim.shortener.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.username}")
    private String username;

    @Value("${keycloak.password}")
    private String password;

    /**
     * Creates Keycloak Admin Client bean.
     *
     * Flow:
     * 1. Read Keycloak credentials from application.yml
     * 2. Build Keycloak client using Admin API credentials
     * 3. Return singleton bean for dependency injection
     *
     * This client is used to:
     * - Create users in Keycloak realm
     * - Check if user already exists
     * - Manage user attributes
     * - Assign roles to users
     *
     * Credentials used:
     * - username: Keycloak admin username (from realm settings)
     * - password: Keycloak admin password (from realm settings)
     * - clientId: Keycloak client ID (from realm clients)
     * - clientSecret: Keycloak client secret (from realm clients)
     */


    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .grantType("password") // Recommended for backend-to-backend calls
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
    }
}
