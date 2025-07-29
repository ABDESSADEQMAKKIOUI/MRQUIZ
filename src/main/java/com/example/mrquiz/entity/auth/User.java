package com.example.mrquiz.entity.auth;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.file.File;
import com.example.mrquiz.enums.UserRole;
import com.example.mrquiz.enums.UserStatus;
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
@Table(name = "users")
public class User extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(unique = true, length = 100)
    private String username;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
    
    // Authentication fields
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;
    
    @Column(name = "mfa_enabled")
    private Boolean mfaEnabled = false;
    
    @Column(name = "mfa_secret", length = 32)
    private String mfaSecret;
    
    // Profile fields
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private File profileImage;
    
    @Column(length = 50)
    private String timezone = "UTC";
    
    @Column(length = 10)
    private String language = "en";
    
    // Security fields
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
    
    @Column(name = "must_change_password")
    private Boolean mustChangePassword = false;
    
    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange = LocalDateTime.now();
    
    // Flexible metadata storage
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> preferences;
    
    // Activity tracking
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
}