package com.example.mrquiz.entity.quiz;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.entity.core.Course;
import com.example.mrquiz.entity.core.Institution;
import com.example.mrquiz.entity.file.File;
import com.example.mrquiz.enums.QuizStatus;
import com.example.mrquiz.enums.QuizType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "quizzes", indexes = {
    @Index(name = "idx_quizzes_creator_status", columnList = "creator_id, status"),
    @Index(name = "idx_quizzes_course", columnList = "course_id, status"),
    @Index(name = "idx_quizzes_availability", columnList = "availability_start, availability_end")
})
public class Quiz extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;
    
    // Basic information
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type", nullable = false)
    private QuizType quizType = QuizType.QUIZ;
    
    // Scoring and grading
    @Column(name = "total_points", precision = 10, scale = 2)
    private BigDecimal totalPoints = BigDecimal.ZERO;
    
    @Column(name = "passing_score", precision = 5, scale = 2)
    private BigDecimal passingScore;
    
    @Column(name = "grading_method", length = 50)
    private String gradingMethod = "automatic";
    
    // Timing
    @Column(name = "time_limit")
    private Integer timeLimit; // Total time limit in minutes
    
    @Column(name = "time_per_question")
    private Integer timePerQuestion; // Time per question in seconds
    
    // Attempt settings
    @Column(name = "attempts_allowed")
    private Integer attemptsAllowed = 1;
    
    @Column(name = "attempt_scoring", length = 50)
    private String attemptScoring = "last";
    
    // Question settings
    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions = false;
    
    @Column(name = "shuffle_answers")
    private Boolean shuffleAnswers = false;
    
    @Column(name = "questions_per_page")
    private Integer questionsPerPage = 1;
    
    // Feedback and results
    @Column(name = "show_results_immediately")
    private Boolean showResultsImmediately = true;
    
    @Column(name = "show_correct_answers")
    private Boolean showCorrectAnswers = true;
    
    @Column(name = "show_explanations")
    private Boolean showExplanations = true;
    
    @Column(name = "allow_review")
    private Boolean allowReview = true;
    
    // Access control
    @Column(name = "availability_start")
    private LocalDateTime availabilityStart;
    
    @Column(name = "availability_end")
    private LocalDateTime availabilityEnd;
    
    private String password; // Optional password protection
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ip_restrictions", columnDefinition = "jsonb")
    private List<String> ipRestrictions;
    
    // Advanced settings
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "security_settings", columnDefinition = "jsonb")
    private Map<String, Object> securitySettings;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "proctoring_settings", columnDefinition = "jsonb")
    private Map<String, Object> proctoringSettings;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "accessibility_settings", columnDefinition = "jsonb")
    private Map<String, Object> accessibilitySettings;
    
    // Media
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_image_id")
    private File bannerImage;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizStatus status = QuizStatus.DRAFT;
}