package com.example.mrquiz.dto.file;

import com.example.mrquiz.enums.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class FileCreateDto {
    @NotBlank(message = "Filename is required")
    @Size(max = 255, message = "Filename must not exceed 255 characters")
    private String filename;

    @NotBlank(message = "Original filename is required")
    @Size(max = 255, message = "Original filename must not exceed 255 characters")
    private String originalFilename;

    @NotBlank(message = "File path is required")
    private String filePath;

    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    private Long fileSize;

    @NotBlank(message = "MIME type is required")
    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    private String mimeType;

    @NotNull(message = "File type is required")
    private FileType fileType;

    @NotBlank(message = "File hash is required")
    @Size(max = 64, message = "File hash must not exceed 64 characters")
    private String fileHash;

    @NotNull(message = "Uploader ID is required")
    private UUID uploadedById;

    private Boolean isPublic = false;

    @Size(max = 50, message = "Storage provider must not exceed 50 characters")
    private String storageProvider = "local";

    @Size(max = 50, message = "Storage region must not exceed 50 characters")
    private String storageRegion;

    @Size(max = 100, message = "Storage bucket must not exceed 100 characters")
    private String storageBucket;

    private Map<String, Object> storageMetadata;
    private Map<String, Object> dimensions;
    private Integer duration;
    private Map<String, Object> encodingInfo;
    private Map<String, Object> accessPermissions;
    private Map<String, Object> processingMetadata;
    private Map<String, Object> processedVariants;
    private LocalDateTime expiresAt;
}