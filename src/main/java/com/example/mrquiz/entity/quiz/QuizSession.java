package com.example.mrquiz.entity.quiz;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "quiz_sessions")
public class QuizSession extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;
    
    // Session identification
    @Column(name = "session_code", unique = true, nullable = false, length = 8)
    private String sessionCode;
    
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Access control
    private String password;
    
    @Column(name = "max_participants")
    private Integer maxParticipants;
    
    @Column(name = "allow_late_join")
    private Boolean allowLateJoin = true;
    
    @Column(name = "require_registration")
    private Boolean requireRegistration = false;
    
    // Timing
    @Column(name = "scheduled_start")
    private LocalDateTime scheduledStart;
    
    @Column(name = "scheduled_end")
    private LocalDateTime scheduledEnd;
    
    @Column(name = "actual_start")
    private LocalDateTime actualStart;
    
    @Column(name = "actual_end")
    private LocalDateTime actualEnd;
    
    // Settings
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings;
    
    @Column(name = "real_time_results")
    private Boolean realTimeResults = false;
    
    @Column(name = "show_leaderboard")
    private Boolean showLeaderboard = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.SCHEDULED;
}