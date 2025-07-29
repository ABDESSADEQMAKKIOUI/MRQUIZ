package com.example.mrquiz.dto.core;

import com.example.mrquiz.enums.CourseStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class CourseResponseDto {
    private UUID id;
    private UUID institutionId;
    private UUID departmentId;
    private String code;
    private String name;
    private String description;
    private String syllabus;
    private Integer credits;
    private String level;
    private Map<String, Object> prerequisites;
    private String semester;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID instructorId;
    private List<UUID> teachingAssistants;
    private Map<String, Object> settings;
    private Map<String, Object> gradingScale;
    private UUID coverImageId;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}