package com.example.mrquiz.dto.quiz;

import com.example.mrquiz.enums.QuizStatus;
import com.example.mrquiz.enums.QuizType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class QuizResponseDto {
    private UUID id;
    private UUID creatorId;
    private UUID courseId;
    private UUID institutionId;
    private String title;
    private String description;
    private String instructions;
    private QuizType quizType;
    private BigDecimal totalPoints;
    private BigDecimal passingScore;
    private String gradingMethod;
    private Integer timeLimit;
    private Integer timePerQuestion;
    private Integer attemptsAllowed;
    private String attemptScoring;
    private Boolean shuffleQuestions;
    private Boolean shuffleAnswers;
    private Integer questionsPerPage;
    private Boolean showResultsImmediately;
    private Boolean showCorrectAnswers;
    private Boolean showExplanations;
    private Boolean allowReview;
    private LocalDateTime availabilityStart;
    private LocalDateTime availabilityEnd;
    private List<String> ipRestrictions;
    private Map<String, Object> settings;
    private Map<String, Object> securitySettings;
    private Map<String, Object> proctoringSettings;
    private Map<String, Object> accessibilitySettings;
    private UUID bannerImageId;
    private QuizStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}