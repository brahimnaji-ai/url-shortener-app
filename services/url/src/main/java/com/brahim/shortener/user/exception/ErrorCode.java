package com.brahim.shortener.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "Passwords do not match.", BAD_REQUEST),
    USERNAME_ALREADY_EXISTS_EXCEPTION("USERNAME_ALREADY_EXISTS_EXCEPTION",
            "Registration cannot be completed. Please try again with different username.", CONFLICT),
    EMAIL_ALREADY_EXISTS_EXCEPTION("EMAIL_ALREADY_EXISTS_EXCEPTION",
            "Registration cannot be completed. Please try again with different email.", CONFLICT),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Username not found", BAD_REQUEST),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid credentials provided", UNAUTHORIZED),

    KEYCLOAK_UNAVAILABLE("KEYCLOAK_UNAVAILABLE", "Service is unavailable", SERVICE_UNAVAILABLE),
    KEYCLOAK_CLIENT_MISCONFIGURED("KEYCLOAK_CLIENT_MISCONFIGURED",
            "Authentication service is misconfigured. Please contact support.", INTERNAL_SERVER_ERROR),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired" , UNAUTHORIZED);
    private String code;
    private String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
