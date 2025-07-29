package com.example.mrquiz.dto.security;

import com.example.mrquiz.enums.SeverityLevel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class AuditLogResponseDto {
    private UUID id;
    private UUID userId;
    private String sessionId;
    private String entityType;
    private UUID entityId;
    private String action;
    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private List<String> changedFields;
    private String ipAddress;
    private String userAgent;
    private String requestMethod;
    private String requestPath;
    private String requestId;
    private Map<String, Object> metadata;
    private SeverityLevel severity;
    private LocalDateTime createdAt;
}