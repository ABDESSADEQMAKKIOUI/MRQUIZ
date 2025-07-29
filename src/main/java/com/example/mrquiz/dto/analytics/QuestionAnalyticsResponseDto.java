package com.example.mrquiz.dto.analytics;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class QuestionAnalyticsResponseDto {
    private UUID id;
    private UUID questionId;
    private UUID quizId;
    private UUID institutionId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer totalAttempts;
    private Integer correctAttempts;
    private Integer partiallyCorrectAttempts;
    private Integer incorrectAttempts;
    private BigDecimal successRate;
    private BigDecimal averageScore;
    private BigDecimal difficultyIndex;
    private BigDecimal discriminationIndex;
    private BigDecimal averageTimeSpent;
    private BigDecimal medianTimeSpent;
    private BigDecimal timeEfficiencyScore;
    private Map<String, Object> answerDistribution;
    private Map<String, Object> commonMistakes;
    private LocalDateTime lastCalculated;
}