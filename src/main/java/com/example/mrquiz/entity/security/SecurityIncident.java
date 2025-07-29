package com.example.mrquiz.entity.security;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.entity.quiz.QuizAttempt;
import com.example.mrquiz.entity.quiz.QuizSession;
import com.example.mrquiz.enums.IncidentType;
import com.example.mrquiz.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "security_incidents", indexes = {
    @Index(name = "idx_security_incidents_user", columnList = "user_id, created_at"),
    @Index(name = "idx_security_incidents_status", columnList = "status, severity")
})
public class SecurityIncident extends BaseEntity {
    
    // Context
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private QuizAttempt attempt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private QuizSession session;
    
    // Incident details
    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", nullable = false)
    private IncidentType incidentType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severity = SeverityLevel.LOW;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Detection
    @Column(name = "detected_by", length = 50)
    private String detectedBy = "system";
    
    @Column(name = "detection_method", length = 100)
    private String detectionMethod;
    
    @Column(name = "confidence_score", precision = 5, scale = 4)
    private BigDecimal confidenceScore;
    
    // Evidence
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> evidence;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "related_logs", columnDefinition = "jsonb")
    private List<UUID> relatedLogs;
    
    // Resolution
    @Column(length = 50)
    private String status = "open";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;
    
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    // Actions taken
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "actions_taken", columnDefinition = "jsonb")
    private Map<String, Object> actionsTaken;
    
    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;
}