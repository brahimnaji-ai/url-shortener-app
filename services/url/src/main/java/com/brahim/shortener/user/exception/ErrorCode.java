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
            "Registration cannot be completed. Please try again with different email.", CONFLICT)

    ;
    private String code;
    private String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
