package com.example.mrquiz.dto.quiz;

import com.example.mrquiz.enums.DifficultyLevel;
import com.example.mrquiz.enums.QuestionStatus;
import com.example.mrquiz.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class QuestionCreateDto {
    @NotNull(message = "Creator ID is required")
    private UUID creatorId;

    private UUID institutionId;
    private UUID courseId;

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    private BigDecimal points = BigDecimal.ONE;
    private BigDecimal negativePoints = BigDecimal.ZERO;

    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    @Size(max = 50, message = "Bloom taxonomy must not exceed 50 characters")
    private String bloomTaxonomy;

    private List<String> learningObjectives;
    private List<String> subjectAreas;
    private List<String> tags;

    private String explanation;
    private String hint;
    private String solutionMethod;

    private List<UUID> questionFiles;

    @NotNull(message = "Question data is required")
    private Map<String, Object> questionData;

    private Map<String, Object> correctAnswers;
    private Map<String, Object> answerValidation;

    private String altText;
    private Map<String, Object> accessibilityMetadata;

    private Integer version = 1;
    private UUID parentQuestionId;

    private Boolean isPublic = false;
    private Map<String, Object> sharingPermissions;

    private QuestionStatus status = QuestionStatus.ACTIVE;
}