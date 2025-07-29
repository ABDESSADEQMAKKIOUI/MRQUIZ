package com.example.mrquiz.dto.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class FileVariantCreateDto {
    @NotNull(message = "Parent file ID is required")
    private UUID parentFileId;

    @NotBlank(message = "Variant name is required")
    @Size(max = 100, message = "Variant name must not exceed 100 characters")
    private String variantName;

    @NotBlank(message = "File path is required")
    private String filePath;

    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    private Long fileSize;

    @NotBlank(message = "MIME type is required")
    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    private String mimeType;

    private Map<String, Object> dimensions;
    private Map<String, Object> processingMetadata;
}