package com.example.mrquiz.dto.quiz;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class QuizAttemptCreateDto {
    @NotNull(message = "Quiz ID is required")
    private UUID quizId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    private UUID sessionId;
    private Integer attemptNumber = 1;
    private Integer timeLimit;
    private String ipAddress;
    private String userAgent;
    private Map<String, Object> browserInfo;
    private Map<String, Object> deviceInfo;
    private Map<String, Object> securityFlags;
    private Map<String, Object> proctoringData;
    private Map<String, Object> metadata;
}