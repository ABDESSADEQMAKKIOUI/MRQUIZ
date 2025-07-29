package com.example.mrquiz.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class UserProfileCreateDto {
    @NotNull(message = "User ID is required")
    private UUID userId;

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
}