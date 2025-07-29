package com.example.mrquiz.entity.quiz;

import com.example.mrquiz.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "question_responses", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"attempt_id", "question_id"}),
       indexes = {
           @Index(name = "idx_question_responses_attempt", columnList = "attempt_id"),
           @Index(name = "idx_question_responses_question", columnList = "question_id")
       })
public class QuestionResponse extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_question_id", nullable = false)
    private QuizQuestion quizQuestion;
    
    // Response data (flexible JSON structure)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> answerData;
    
    // Grading information
    @Column(name = "points_earned", precision = 5, scale = 2)
    private BigDecimal pointsEarned;
    
    @Column(name = "max_points", precision = 5, scale = 2)
    private BigDecimal maxPoints;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "partial_credit", precision = 5, scale = 2)
    private BigDecimal partialCredit;
    
    // Response metadata
    @Column(name = "time_spent")
    private Integer timeSpent = 0; // Time spent on this question in seconds
    
    @Column(name = "attempt_count")
    private Integer attemptCount = 1; // Number of times answer was changed
    
    @Column(name = "confidence_level")
    private Integer confidenceLevel; // Student's confidence (1-5)
    
    @Column(name = "flagged_for_review")
    private Boolean flaggedForReview = false;
    
    // Timestamps
    @Column(name = "first_answered_at")
    private LocalDateTime firstAnsweredAt;
    
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt = LocalDateTime.now();
    
    // Additional metadata
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_metadata", columnDefinition = "jsonb")
    private Map<String, Object> responseMetadata; // Keystroke data, mouse movements, etc.
}