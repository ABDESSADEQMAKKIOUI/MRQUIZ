package com.example.mrquiz.dto.analytics;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class QuestionAnalyticsUpdateDto {
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
}