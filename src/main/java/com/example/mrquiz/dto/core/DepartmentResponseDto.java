package com.example.mrquiz.dto.core;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class DepartmentResponseDto {
    private UUID id;
    private UUID institutionId;
    private UUID parentDepartmentId;
    private String name;
    private String code;
    private String description;
    private UUID headUserId;
    private Map<String, Object> settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}