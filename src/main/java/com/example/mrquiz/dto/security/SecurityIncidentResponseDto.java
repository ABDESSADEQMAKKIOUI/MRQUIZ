package com.example.mrquiz.dto.security;

import com.example.mrquiz.enums.IncidentType;
import com.example.mrquiz.enums.SeverityLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class SecurityIncidentResponseDto {
    private UUID id;
    private UUID userId;
    private UUID attemptId;
    private UUID sessionId;
    private IncidentType incidentType;
    private SeverityLevel severity;
    private String title;
    private String description;
    private String detectedBy;
    private String detectionMethod;
    private BigDecimal confidenceScore;
    private Map<String, Object> evidence;
    private List<UUID> relatedLogs;
    private String status;
    private UUID assignedToId;
    private UUID resolvedById;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private Map<String, Object> actionsTaken;
    private Boolean followUpRequired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}