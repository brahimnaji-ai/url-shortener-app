package com.brahim.shortener.user;

import com.brahim.shortener.user.dto.request.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User toUser(RegisterRequest request, String keycloakUserId ) {
        return User.builder()
                .keycloakId(keycloakUserId)
                .username(request.username())
                .email(request.email())
                .build();
    }
}
