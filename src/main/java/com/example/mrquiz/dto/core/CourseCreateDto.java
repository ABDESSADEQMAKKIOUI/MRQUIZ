package com.example.mrquiz.dto.core;

import com.example.mrquiz.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class CourseCreateDto {
    @NotNull(message = "Institution ID is required")
    private UUID institutionId;

    private UUID departmentId;

    @NotBlank(message = "Code is required")
    @Size(max = 20, message = "Code must not exceed 20 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;
    private String syllabus;
    private Integer credits;

    @Size(max = 20, message = "Level must not exceed 20 characters")
    private String level;

    private Map<String, Object> prerequisites;

    @Size(max = 50, message = "Semester must not exceed 50 characters")
    private String semester;

    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    private String academicYear;

    private LocalDate startDate;
    private LocalDate endDate;
    private UUID instructorId;
    private List<UUID> teachingAssistants;
    private Map<String, Object> settings;
    private Map<String, Object> gradingScale;
    private UUID coverImageId;
    private CourseStatus status = CourseStatus.DRAFT;
}