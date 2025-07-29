package com.example.mrquiz.dto.auth;

import com.example.mrquiz.enums.TokenType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class AuthTokenResponseDto {
    private UUID id;
    private UUID userId;
    private TokenType tokenType;
    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}