package com.example.mrquiz.dto.auth;

import com.example.mrquiz.enums.UserRole;
import com.example.mrquiz.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private UserRole role;
    private UserStatus status;
    private Boolean emailVerified;
    private String phone;
    private Boolean phoneVerified;
    private Boolean mfaEnabled;
    private UUID profileImageId;
    private String timezone;
    private String language;
    private Integer failedLoginAttempts;
    private LocalDateTime lockedUntil;
    private Boolean mustChangePassword;
    private LocalDateTime lastPasswordChange;
    private Map<String, Object> metadata;
    private Map<String, Object> preferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private LocalDateTime lastActivity;
}