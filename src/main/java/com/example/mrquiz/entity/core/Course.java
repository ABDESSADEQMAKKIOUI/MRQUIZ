package com.example.mrquiz.entity.core;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.entity.file.File;
import com.example.mrquiz.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "courses", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"institution_id", "code", "academic_year", "semester"}),
       indexes = {
           @Index(name = "idx_courses_instructor", columnList = "instructor_id, status")
       })
public class Course extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Course identification
    @Column(nullable = false, length = 20)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String syllabus;
    
    // Academic details
    private Integer credits;
    
    @Column(length = 20)
    private String level; // undergraduate, graduate, professional
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> prerequisites;
    
    // Schedule
    @Column(length = 50)
    private String semester;
    
    @Column(name = "academic_year", length = 20)
    private String academicYear;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    // Instructor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private User instructor;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "teaching_assistants", columnDefinition = "jsonb")
    private List<UUID> teachingAssistants; // Array of user IDs
    
    // Configuration
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "grading_scale", columnDefinition = "jsonb")
    private Map<String, Object> gradingScale;
    
    // Media
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_image_id")
    private File coverImage;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.DRAFT;
}