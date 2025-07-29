package com.example.mrquiz.dto.file;

import com.example.mrquiz.enums.FileStatus;
import com.example.mrquiz.enums.MediaProcessingStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class FileUpdateDto {
    @Size(max = 255, message = "Filename must not exceed 255 characters")
    private String filename;

    private FileStatus status;
    private Boolean isPublic;

    @Size(max = 50, message = "Storage provider must not exceed 50 characters")
    private String storageProvider;

    @Size(max = 50, message = "Storage region must not exceed 50 characters")
    private String storageRegion;

    @Size(max = 100, message = "Storage bucket must not exceed 100 characters")
    private String storageBucket;

    private Map<String, Object> storageMetadata;
    private Map<String, Object> dimensions;
    private Integer duration;
    private Map<String, Object> encodingInfo;
    private Map<String, Object> accessPermissions;
    private MediaProcessingStatus processingStatus;
    private Map<String, Object> processingMetadata;
    private Map<String, Object> processedVariants;
    private LocalDateTime expiresAt;
    private LocalDateTime archivedAt;
}