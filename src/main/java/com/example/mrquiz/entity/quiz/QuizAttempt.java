package com.example.mrquiz.entity.quiz;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.enums.AttemptStatus;
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
@Table(name = "quiz_attempts", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"quiz_id", "user_id", "attempt_number"}),
       indexes = {
           @Index(name = "idx_quiz_attempts_user_quiz", columnList = "user_id, quiz_id"),
           @Index(name = "idx_quiz_attempts_status", columnList = "status, created_at"),
           @Index(name = "idx_quiz_attempts_session", columnList = "session_id")
       })
public class QuizAttempt extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private QuizSession session;
    
    // Attempt information
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;
    
    // Scoring
    @Column(precision = 10, scale = 2)
    private BigDecimal score;
    
    @Column(name = "total_points", precision = 10, scale = 2)
    private BigDecimal totalPoints;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;
    
    @Column(length = 5)
    private String grade;
    
    private Boolean passed;
    
    // Timing
    @Column(name = "time_limit")
    private Integer timeLimit; // Actual time limit used (in minutes)
    
    @Column(name = "time_spent")
    private Integer timeSpent = 0; // Total time spent in seconds
    
    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    // Technical information
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "browser_info", columnDefinition = "jsonb")
    private Map<String, Object> browserInfo;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "device_info", columnDefinition = "jsonb")
    private Map<String, Object> deviceInfo;
    
    // Security and proctoring
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "security_flags", columnDefinition = "jsonb")
    private Map<String, Object> securityFlags;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "proctoring_data", columnDefinition = "jsonb")
    private Map<String, Object> proctoringData;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "suspicious_activities", columnDefinition = "jsonb")
    private Map<String, Object> suspiciousActivities;
    
    // Additional metadata
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}