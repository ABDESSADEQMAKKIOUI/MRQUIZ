package com.example.mrquiz.entity.core;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.enums.InstitutionRole;
import com.example.mrquiz.enums.MembershipStatus;
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
@Table(name = "user_institutions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "institution_id"}),
       indexes = @Index(name = "idx_user_institutions_active", columnList = "user_id, institution_id, status"))
public class UserInstitution extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstitutionRole role;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status = MembershipStatus.ACTIVE;
    
    // Additional identification
    @Column(name = "student_id", length = 50)
    private String studentId;
    
    @Column(name = "employee_id", length = 50)
    private String employeeId;
    
    // Permissions
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> permissions;
    
    // Timestamps
    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();
    
    @Column(name = "left_at")
    private LocalDateTime leftAt;
}