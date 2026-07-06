package com.brahim.shortener.keycloak;

import com.brahim.shortener.keycloak.exception.KeycloakUserCreationException;
import com.brahim.shortener.user.dto.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService{
    private final Keycloak keycloak;
    @Value("${keycloak.realm}")
    private String realm;
    /**
     * Creates a user in Keycloak realm.
     *
     * Flow:
     * 1. Build UserRepresentation with username, email
     * 2. Set password (as temporary credential)
     * 3. Send to Keycloak Admin API
     * 4. Extract and return Keycloak user ID from response
     */


    @Override
    public String createUser(RegisterRequest request) {
        try {
            // 1-2 Build keycloak user object & Set password
            UserRepresentation user = getUserRepresentation(request);

            // 3. send to keycloak
            var response = keycloak.realm(realm)
                    .users()
                    .create(user);

            // 4. extract user id from the response header
            // [Location: http://localhost:8080/admin/realms/{realm-name}/users/{generated-user-uuid}]
            String location = response.getLocation().toString();
            String keycloakUserId = location.substring(location.lastIndexOf('/')+1);
            log.info("User created in Keycloak: username={}, keycloakId={}", request.username(), keycloakUserId);
            return keycloakUserId;

        }catch (Exception e){
            log.error("Failed to create user in Keycloak: {}", e.getMessage());
            throw new KeycloakUserCreationException("Could not create user in Keycloak");
        }
    }

    private static @NonNull UserRepresentation getUserRepresentation(RegisterRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(request.password());
        credentials.setTemporary(false);
        user.setCredentials(List.of(credentials));
        return user;
    }


    @Override
    public boolean userExists(String username) {
        try {
            var users = keycloak.realm(realm)
                    .users()
                    .search(username,true);
            boolean exists = !users.isEmpty();
            log.error("Username '{}' exists: {}", username, exists);
            return exists;
        }catch (Exception e){
            log.error("Error checking if user exists: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean emailExists(String email) {
        try{
            var users = keycloak.realm(realm)
                    .users()
                    .searchByEmail(email, true);
            boolean exists = !users.isEmpty();
            log.debug("Email '{}' exists: {}", email, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking if email exists: {}", e.getMessage());
            return false;
        }
    }
}
