package com.example.mrquiz.dto.auth;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class UserProfileResponseDto {
    private UUID userId;
    private String bio;
    private String website;
    private String linkedinUrl;
    private String twitterHandle;
    private Map<String, Object> academicInfo;
    private Map<String, Object> professionalInfo;
    private Map<String, Object> certifications;
    private Map<String, Object> notificationPreferences;
    private Map<String, Object> privacySettings;
    private Map<String, Object> accessibilitySettings;
    private Integer totalQuizzesTaken;
    private Integer totalQuizzesCreated;
    private BigDecimal averageScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}