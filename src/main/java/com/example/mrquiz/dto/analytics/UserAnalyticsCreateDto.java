package com.example.mrquiz.dto.analytics;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class UserAnalyticsCreateDto {
    @NotNull(message = "User ID is required")
    private UUID userId;

    private UUID courseId;
    private UUID institutionId;

    @NotNull(message = "Period start is required")
    private LocalDate periodStart;

    @NotNull(message = "Period end is required")
    private LocalDate periodEnd;

    @Size(max = 100, message = "Subject area must not exceed 100 characters")
    private String subjectArea;

    private Integer totalQuizzesTaken = 0;
    private Integer totalQuizzesCompleted = 0;
    private Integer totalQuestionsAnswered = 0;
    private Integer correctAnswers = 0;
    private Integer partiallyCorrectAnswers = 0;

    private BigDecimal averageScore;
    private BigDecimal medianScore;
    private BigDecimal bestScore;
    private BigDecimal worstScore;
    private BigDecimal improvementTrend;
    private BigDecimal consistencyScore;

    private Integer totalTimeSpent = 0;
    private Integer averageTimePerQuiz;
    private BigDecimal timeEfficiencyScore;

    private Map<String, Object> preferredQuestionTypes;
    private List<String> strongestTopics;
    private List<String> weakestTopics;
    private BigDecimal learningVelocity;

    private BigDecimal participationRate;
    private BigDecimal completionRate;
    private Integer reviewFrequency = 0;
}