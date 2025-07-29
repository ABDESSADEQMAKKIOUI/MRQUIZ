package com.example.mrquiz.dto.auth;

import com.example.mrquiz.enums.TokenType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class AuthTokenCreateDto {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Token type is required")
    private TokenType tokenType;

    @NotNull(message = "Expiration time is required")
    private LocalDateTime expiresAt;

    private Map<String, Object> metadata;
}