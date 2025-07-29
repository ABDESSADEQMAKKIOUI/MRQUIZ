package com.example.mrquiz.dto.analytics;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
public class QuestionAnalyticsCreateDto {
    @NotNull(message = "Question ID is required")
    private UUID questionId;

    private UUID quizId;
    private UUID institutionId;

    @NotNull(message = "Period start is required")
    private LocalDate periodStart;

    @NotNull(message = "Period end is required")
    private LocalDate periodEnd;

    private Integer totalAttempts = 0;
    private Integer correctAttempts = 0;
    private Integer partiallyCorrectAttempts = 0;
    private Integer incorrectAttempts = 0;

    private BigDecimal successRate;
    private BigDecimal averageScore;
    private BigDecimal difficultyIndex;
    private BigDecimal discriminationIndex;

    private BigDecimal averageTimeSpent;
    private BigDecimal medianTimeSpent;
    private BigDecimal timeEfficiencyScore;

    private Map<String, Object> answerDistribution;
    private Map<String, Object> commonMistakes;
}