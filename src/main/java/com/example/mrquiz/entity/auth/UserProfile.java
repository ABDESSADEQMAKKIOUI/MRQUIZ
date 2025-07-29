package com.example.mrquiz.entity.auth;

import com.example.mrquiz.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @MapsId
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(length = 255)
    private String website;
    
    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;
    
    @Column(name = "twitter_handle", length = 50)
    private String twitterHandle;
    
    // Academic and professional information stored as JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "academic_info", columnDefinition = "jsonb")
    private Map<String, Object> academicInfo;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "professional_info", columnDefinition = "jsonb")
    private Map<String, Object> professionalInfo;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> certifications;
    
    // Preferences
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_preferences", columnDefinition = "jsonb")
    private Map<String, Object> notificationPreferences;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "privacy_settings", columnDefinition = "jsonb")
    private Map<String, Object> privacySettings;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "accessibility_settings", columnDefinition = "jsonb")
    private Map<String, Object> accessibilitySettings;
    
    // Statistics
    @Column(name = "total_quizzes_taken")
    private Integer totalQuizzesTaken = 0;
    
    @Column(name = "total_quizzes_created")
    private Integer totalQuizzesCreated = 0;
    
    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;
}