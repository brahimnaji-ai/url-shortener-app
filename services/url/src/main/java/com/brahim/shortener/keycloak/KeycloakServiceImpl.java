package com.brahim.shortener.keycloak;

    import com.brahim.shortener.keycloak.exception.KeycloakUserCreationException;
import com.brahim.shortener.user.dto.request.RegisterRequest;
import com.brahim.shortener.user.dto.response.LoginResponse;
import com.brahim.shortener.user.dto.response.RefreshResponse;
import com.brahim.shortener.user.exception.BusinessException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.brahim.shortener.user.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService{
    private final Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;


    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.token-endpoint}")
    private String tokenEndpoint;

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
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(request.password());
        credentials.setTemporary(false);
        user.setCredentials(List.of(credentials));

        log.info("Enabled: {}", user.isEnabled());
        log.info("Email verified: {}", user.isEmailVerified());
        log.info("Required actions: {}", user.getRequiredActions());
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

    @Override
    public LoginResponse login(String username, String password) {
        try {
            var userKeycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(username)
                    .password(password)
                    .build();

            AccessTokenResponse token = userKeycloak.tokenManager().getAccessToken();

            return new LoginResponse(
                    token.getToken(),
                    token.getRefreshToken(),
                    token.getExpiresIn(),
                    token.getTokenType()
            );

        } catch (NotAuthorizedException e) {
            // 401 — wrong username or password
            log.warn("Login failed for user '{}': invalid credentials", username);
            throw new BusinessException(INVALID_CREDENTIALS);
        } catch (BadRequestException e) {
            // 400 — client config issue (e.g. Direct Access Grants disabled on Keycloak client)
            log.error("Keycloak rejected login request for user '{}' (HTTP 400). " +
                    "Check that 'Direct Access Grants' is enabled for client '{}' in realm '{}'.",
                    username, clientId, realm);
            throw new BusinessException(KEYCLOAK_CLIENT_MISCONFIGURED);
        } catch (Exception e) {
            // Network error, Keycloak down, timeout, etc.
            log.error("Keycloak unreachable during login for user '{}': {}", username, e.getMessage());
            throw new BusinessException(KEYCLOAK_UNAVAILABLE);
        }
    }

    @Override
    public RefreshResponse refresh(String refreshToken) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        String body = "grant_type=refresh_token"
                + "&refresh_token="+ URLEncoder.encode(refreshToken, StandardCharsets.UTF_8)
                + "&client_id="+clientId
                + "&client_secret="+clientSecret;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode()!=200){
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(response.body());

        return new RefreshResponse(
            node.get("access_token").asString(),
            node.get("refresh_token").asString()
        );
    }
}
