package com.example.mrquiz.dto.core;

import com.example.mrquiz.enums.InstitutionStatus;
import com.example.mrquiz.enums.InstitutionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class InstitutionResponseDto {
    private UUID id;
    private String name;
    private String slug;
    private InstitutionType type;
    private String email;
    private String phone;
    private String website;
    private Map<String, Object> address;
    private String domain;
    private UUID logoId;
    private Map<String, Object> settings;
    private Map<String, Object> branding;
    private Map<String, Object> features;
    private Map<String, Object> limits;
    private InstitutionStatus status;
    private String subscriptionTier;
    private LocalDateTime trialEndsAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}