package com.example.mrquiz.entity.core;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.enums.EnrollmentStatus;
import com.example.mrquiz.enums.EnrollmentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "course_enrollments", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "user_id"}),
       indexes = {
           @Index(name = "idx_course_enrollments_student", columnList = "user_id, status"),
           @Index(name = "idx_course_enrollments_course", columnList = "course_id, status")
       })
public class CourseEnrollment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_type", nullable = false)
    private EnrollmentType enrollmentType = EnrollmentType.REGULAR;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
    
    // Grading
    @Column(name = "final_grade", precision = 5, scale = 2)
    private BigDecimal finalGrade;
    
    @Column(name = "grade_letter", length = 5)
    private String gradeLetter;
    
    @Column(name = "gpa_points", precision = 3, scale = 2)
    private BigDecimal gpaPoints;
    
    // Timestamps
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt = LocalDateTime.now();
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}