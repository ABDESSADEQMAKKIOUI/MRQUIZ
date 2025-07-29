package com.example.mrquiz.entity.quiz;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.entity.core.Course;
import com.example.mrquiz.entity.core.Institution;
import com.example.mrquiz.enums.DifficultyLevel;
import com.example.mrquiz.enums.QuestionStatus;
import com.example.mrquiz.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_questions_creator_status", columnList = "creator_id, status"),
    @Index(name = "idx_questions_type_difficulty", columnList = "question_type, difficulty_level"),
    @Index(name = "idx_questions_tags", columnList = "tags")
})
public class Question extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    
    // Question content
    private String title;
    
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;
    
    // Grading
    @Column(precision = 5, scale = 2)
    private BigDecimal points = BigDecimal.ONE;
    
    @Column(name = "negative_points", precision = 5, scale = 2)
    private BigDecimal negativePoints = BigDecimal.ZERO;
    
    // Classification
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;
    
    @Column(name = "bloom_taxonomy", length = 50)
    private String bloomTaxonomy;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "learning_objectives", columnDefinition = "jsonb")
    private List<String> learningObjectives;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "subject_areas", columnDefinition = "jsonb")
    private List<String> subjectAreas;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;
    
    // Content and explanation
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @Column(columnDefinition = "TEXT")
    private String hint;
    
    @Column(name = "solution_method", columnDefinition = "TEXT")
    private String solutionMethod;
    
    // Media attachments
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_files", columnDefinition = "jsonb")
    private List<UUID> questionFiles; // Array of file IDs
    
    // Question-specific data (flexible JSON structure)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> questionData;
    
    // Answer validation for auto-grading
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "correct_answers", columnDefinition = "jsonb")
    private Map<String, Object> correctAnswers;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_validation", columnDefinition = "jsonb")
    private Map<String, Object> answerValidation;
    
    // Usage and analytics
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
    @Column(name = "difficulty_index", precision = 5, scale = 4)
    private BigDecimal difficultyIndex;
    
    @Column(name = "discrimination_index", precision = 5, scale = 4)
    private BigDecimal discriminationIndex;
    
    // Accessibility
    @Column(name = "alt_text", columnDefinition = "TEXT")
    private String altText;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "accessibility_metadata", columnDefinition = "jsonb")
    private Map<String, Object> accessibilityMetadata;
    
    // Versioning
    @Column
    private Integer version = 1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_question_id")
    private Question parentQuestion;
    
    // Sharing and permissions
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sharing_permissions", columnDefinition = "jsonb")
    private Map<String, Object> sharingPermissions;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status = QuestionStatus.ACTIVE;
}