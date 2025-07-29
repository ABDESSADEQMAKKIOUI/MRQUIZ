package com.example.mrquiz.dto.quiz;

import com.example.mrquiz.enums.QuizStatus;
import com.example.mrquiz.enums.QuizType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class QuizUpdateDto {
    private UUID courseId;
    private UUID institutionId;

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;
    private String instructions;
    private QuizType quizType;
    private BigDecimal totalPoints;
    private BigDecimal passingScore;

    @Size(max = 50, message = "Grading method must not exceed 50 characters")
    private String gradingMethod;

    @Positive(message = "Time limit must be positive")
    private Integer timeLimit;

    @Positive(message = "Time per question must be positive")
    private Integer timePerQuestion;

    @Positive(message = "Attempts allowed must be positive")
    private Integer attemptsAllowed;

    @Size(max = 50, message = "Attempt scoring must not exceed 50 characters")
    private String attemptScoring;

    private Boolean shuffleQuestions;
    private Boolean shuffleAnswers;

    @Positive(message = "Questions per page must be positive")
    private Integer questionsPerPage;

    private Boolean showResultsImmediately;
    private Boolean showCorrectAnswers;
    private Boolean showExplanations;
    private Boolean allowReview;

    private LocalDateTime availabilityStart;
    private LocalDateTime availabilityEnd;
    private String password;
    private List<String> ipRestrictions;

    private Map<String, Object> settings;
    private Map<String, Object> securitySettings;
    private Map<String, Object> proctoringSettings;
    private Map<String, Object> accessibilitySettings;

    private UUID bannerImageId;
    private QuizStatus status;
}