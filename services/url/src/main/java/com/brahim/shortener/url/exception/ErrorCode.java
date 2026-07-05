package com.brahim.shortener.url.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    URL_NOT_FOUND("URL_NOT_FOUND","Url not found", NOT_FOUND),
    URL_EXPIRED("URL_EXPIRED", "Short URL expired, you msut generate new one", BAD_REQUEST)
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
