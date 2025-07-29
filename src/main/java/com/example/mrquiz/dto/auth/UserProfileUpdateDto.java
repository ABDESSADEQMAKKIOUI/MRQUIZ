package com.example.mrquiz.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class UserProfileUpdateDto {
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    private String website;

    @Size(max = 255, message = "LinkedIn URL must not exceed 255 characters")
    private String linkedinUrl;

    @Size(max = 50, message = "Twitter handle must not exceed 50 characters")
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
}