package com.example.mrquiz.dto.quiz;

import com.example.mrquiz.enums.DifficultyLevel;
import com.example.mrquiz.enums.QuestionStatus;
import com.example.mrquiz.enums.QuestionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class QuestionResponseDto {
    private UUID id;
    private UUID creatorId;
    private UUID institutionId;
    private UUID courseId;
    private String title;
    private String questionText;
    private QuestionType questionType;
    private BigDecimal points;
    private BigDecimal negativePoints;
    private DifficultyLevel difficultyLevel;
    private String bloomTaxonomy;
    private List<String> learningObjectives;
    private List<String> subjectAreas;
    private List<String> tags;
    private String explanation;
    private String hint;
    private String solutionMethod;
    private List<UUID> questionFiles;
    private Map<String, Object> questionData;
    private Map<String, Object> correctAnswers;
    private Map<String, Object> answerValidation;
    private Integer usageCount;
    private BigDecimal difficultyIndex;
    private BigDecimal discriminationIndex;
    private String altText;
    private Map<String, Object> accessibilityMetadata;
    private Integer version;
    private UUID parentQuestionId;
    private Boolean isPublic;
    private Map<String, Object> sharingPermissions;
    private QuestionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}