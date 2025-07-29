package com.example.mrquiz.dto.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class DepartmentCreateDto {
    @NotNull(message = "Institution ID is required")
    private UUID institutionId;

    private UUID parentDepartmentId;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 20, message = "Code must not exceed 20 characters")
    private String code;

    private String description;
    private UUID headUserId;
    private Map<String, Object> settings;
}