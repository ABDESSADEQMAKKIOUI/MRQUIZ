package com.example.mrquiz.dto.analytics;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class UserAnalyticsResponseDto {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private UUID institutionId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String subjectArea;
    private Integer totalQuizzesTaken;
    private Integer totalQuizzesCompleted;
    private Integer totalQuestionsAnswered;
    private Integer correctAnswers;
    private Integer partiallyCorrectAnswers;
    private BigDecimal averageScore;
    private BigDecimal medianScore;
    private BigDecimal bestScore;
    private BigDecimal worstScore;
    private BigDecimal improvementTrend;
    private BigDecimal consistencyScore;
    private Integer totalTimeSpent;
    private Integer averageTimePerQuiz;
    private BigDecimal timeEfficiencyScore;
    private Map<String, Object> preferredQuestionTypes;
    private List<String> strongestTopics;
    private List<String> weakestTopics;
    private BigDecimal learningVelocity;
    private BigDecimal participationRate;
    private BigDecimal completionRate;
    private Integer reviewFrequency;
    private LocalDateTime lastUpdated;
}