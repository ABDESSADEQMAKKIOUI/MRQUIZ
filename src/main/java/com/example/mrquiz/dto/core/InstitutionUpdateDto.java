package com.example.mrquiz.dto.core;

import com.example.mrquiz.enums.InstitutionStatus;
import com.example.mrquiz.enums.InstitutionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class InstitutionUpdateDto {
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 100, message = "Slug must not exceed 100 characters")
    private String slug;

    private InstitutionType type;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Size(max = 255, message = "Website must not exceed 255 characters")
    private String website;

    private Map<String, Object> address;

    @Size(max = 100, message = "Domain must not exceed 100 characters")
    private String domain;

    private UUID logoId;
    private Map<String, Object> settings;
    private Map<String, Object> branding;
    private Map<String, Object> features;
    private Map<String, Object> limits;
    private InstitutionStatus status;

    @Size(max = 50, message = "Subscription tier must not exceed 50 characters")
    private String subscriptionTier;

    private LocalDateTime trialEndsAt;
}