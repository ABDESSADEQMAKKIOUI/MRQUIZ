package com.example.mrquiz.dto.security;

import com.example.mrquiz.enums.SeverityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class AuditLogCreateDto {
    private UUID userId;

    @Size(max = 255, message = "Session ID must not exceed 255 characters")
    private String sessionId;

    @NotBlank(message = "Entity type is required")
    @Size(max = 50, message = "Entity type must not exceed 50 characters")
    private String entityType;

    private UUID entityId;

    @NotBlank(message = "Action is required")
    @Size(max = 50, message = "Action must not exceed 50 characters")
    private String action;

    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private List<String> changedFields;

    private String ipAddress;

    private String userAgent;

    @Size(max = 10, message = "Request method must not exceed 10 characters")
    private String requestMethod;

    private String requestPath;

    @Size(max = 100, message = "Request ID must not exceed 100 characters")
    private String requestId;

    private Map<String, Object> metadata;
    private SeverityLevel severity = SeverityLevel.INFO;
}