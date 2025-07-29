package com.example.mrquiz.dto.core;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class DepartmentUpdateDto {
    private UUID parentDepartmentId;

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 20, message = "Code must not exceed 20 characters")
    private String code;

    private String description;
    private UUID headUserId;
    private Map<String, Object> settings;
}