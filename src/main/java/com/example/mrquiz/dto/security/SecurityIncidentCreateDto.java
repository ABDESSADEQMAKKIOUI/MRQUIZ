package com.example.mrquiz.dto.security;

import com.example.mrquiz.enums.IncidentType;
import com.example.mrquiz.enums.SeverityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class SecurityIncidentCreateDto {
    private UUID userId;
    private UUID attemptId;
    private UUID sessionId;

    @NotNull(message = "Incident type is required")
    private IncidentType incidentType;

    private SeverityLevel severity = SeverityLevel.LOW;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @Size(max = 50, message = "Detected by must not exceed 50 characters")
    private String detectedBy = "system";

    @Size(max = 100, message = "Detection method must not exceed 100 characters")
    private String detectionMethod;

    private BigDecimal confidenceScore;

    private Map<String, Object> evidence;
    private List<UUID> relatedLogs;

    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status = "open";

    private UUID assignedToId;

    private Map<String, Object> actionsTaken;
    private Boolean followUpRequired = false;
}