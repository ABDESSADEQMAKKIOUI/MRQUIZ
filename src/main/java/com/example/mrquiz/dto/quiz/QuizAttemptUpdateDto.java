package com.example.mrquiz.dto.quiz;

import com.example.mrquiz.enums.AttemptStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class QuizAttemptUpdateDto {
    private AttemptStatus status;
    private BigDecimal score;
    private BigDecimal totalPoints;
    private BigDecimal percentage;
    private String grade;
    private Boolean passed;
    private Integer timeSpent;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private Map<String, Object> securityFlags;
    private Map<String, Object> proctoringData;
    private Map<String, Object> suspiciousActivities;
    private Map<String, Object> metadata;
}