package com.brahim.shortener.user;

import com.brahim.shortener.keycloak.KeycloakServiceImpl;
import com.brahim.shortener.user.dto.request.LoginRequest;
import com.brahim.shortener.user.dto.request.RefreshRequest;
import com.brahim.shortener.user.dto.request.RegisterRequest;
import com.brahim.shortener.user.dto.response.LoginResponse;
import com.brahim.shortener.user.dto.response.RefreshResponse;
import com.brahim.shortener.user.dto.response.RegisterResponse;
import com.brahim.shortener.user.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.brahim.shortener.user.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository repository;
    private final KeycloakServiceImpl keycloakService;
    private final UserMapper mapper;

    /**
     * Registration flow:
     *
     * 1. Validate uniqueness (username & email) in Keycloak
     * 2. Create user in Keycloak realm
     * 3. Create User entity in local database
     * 4. Return success response
     *
     * If ANY step fails, throw exception.
     * Transaction ensures consistency:
     * - If Keycloak creation succeeds but DB save fails → rollback won't affect Keycloak
     *   (we accept this: Keycloak is source of truth, orphaned users are cleaned up by admins)
     */

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 1. Check if passwords match
        if(!request.password().equals(request.passwordConfirm())){
            throw new BusinessException(PASSWORD_MISMATCH);
        }
        // 2.  Do the 2 checks
        if(keycloakService.userExists(request.username())){
            throw new BusinessException(USERNAME_ALREADY_EXISTS_EXCEPTION);
        }

        if(keycloakService.emailExists(request.email())){
            throw new BusinessException(EMAIL_ALREADY_EXISTS_EXCEPTION);
        }

        // 3. Register user in keycloak

        var keycloakUserId = keycloakService.createUser(request);

        // 4. save user locally in postgres db
        User user = mapper.toUser(request, keycloakUserId);
        User savedUser = repository.save(user);
        log.info("User registered successfully: id={}, username={}", savedUser.getId(), savedUser.getUsername());

        // 5. return RegisterResponse record

        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
    "Registration successful. Please verify your email."
        );
    }




    public LoginResponse login(LoginRequest request) {
        return keycloakService.login(request.username(), request.password());
    }

    public RefreshResponse refresh(RefreshRequest request) throws IOException, InterruptedException {
        return keycloakService.refresh(request.refreshToken());
    }
}
