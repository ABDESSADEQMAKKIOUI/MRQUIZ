package com.example.mrquiz.entity.security;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_logs_user_created", columnList = "user_id, created_at"),
    @Index(name = "idx_audit_logs_entity", columnList = "entity_type, entity_id, created_at")
})
public class AuditLog extends BaseEntity {
    
    // Actor information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "session_id")
    private String sessionId;
    
    // Action details
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;
    
    @Column(name = "entity_id")
    private UUID entityId;
    
    @Column(nullable = false, length = 50)
    private String action;
    
    // Change tracking
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_values", columnDefinition = "jsonb")
    private Map<String, Object> oldValues;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", columnDefinition = "jsonb")
    private Map<String, Object> newValues;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "changed_fields", columnDefinition = "jsonb")
    private List<String> changedFields;
    
    // Request context
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "request_method", length = 10)
    private String requestMethod;
    
    @Column(name = "request_path", columnDefinition = "TEXT")
    private String requestPath;
    
    @Column(name = "request_id", length = 100)
    private String requestId;
    
    // Additional context
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severity = SeverityLevel.INFO;
}