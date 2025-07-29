package com.example.mrquiz.dto.quiz;

import com.example.mrquiz.enums.AttemptStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class QuizAttemptResponseDto {
    private UUID id;
    private UUID quizId;
    private UUID userId;
    private UUID sessionId;
    private Integer attemptNumber;
    private AttemptStatus status;
    private BigDecimal score;
    private BigDecimal totalPoints;
    private BigDecimal percentage;
    private String grade;
    private Boolean passed;
    private Integer timeLimit;
    private Integer timeSpent;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private String ipAddress;
    private String userAgent;
    private Map<String, Object> browserInfo;
    private Map<String, Object> deviceInfo;
    private Map<String, Object> securityFlags;
    private Map<String, Object> proctoringData;
    private Map<String, Object> suspiciousActivities;
    private Map<String, Object> metadata;
}