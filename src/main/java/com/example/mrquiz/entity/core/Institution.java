package com.example.mrquiz.entity.core;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.file.File;
import com.example.mrquiz.enums.InstitutionStatus;
import com.example.mrquiz.enums.InstitutionType;
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
@Table(name = "institutions", indexes = {
    @Index(name = "idx_institutions_domain", columnList = "domain"),
    @Index(name = "idx_institutions_status", columnList = "status")
})
public class Institution extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, length = 100)
    private String slug; // URL-friendly identifier
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstitutionType type;
    
    // Contact information
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    private String website;
    
    // Address stored as JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> address;
    
    // Configuration
    @Column(length = 100)
    private String domain; // Email domain for auto-enrollment
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id")
    private File logo;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> branding;
    
    // Features and limits
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> features;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> limits;
    
    // Status and billing
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstitutionStatus status = InstitutionStatus.ACTIVE;
    
    @Column(name = "subscription_tier", length = 50)
    private String subscriptionTier = "basic";
    
    @Column(name = "trial_ends_at")
    private LocalDateTime trialEndsAt;
}