package com.example.mrquiz.dto.file;

import com.example.mrquiz.enums.FileStatus;
import com.example.mrquiz.enums.FileType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class FileResponseDto {
    private UUID id;
    private String filename;
    private String originalFilename;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private FileType fileType;
    private String fileHash;
    private FileStatus status;
    private UUID uploadedById;
    private Boolean isPublic;
    private String storageProvider;
    private String storageRegion;
    private String storageBucket;
    private Map<String, Object> storageMetadata;
    private Map<String, Object> dimensions;
    private Integer duration;
    private Map<String, Object> encodingInfo;
    private Map<String, Object> accessPermissions;
    private Map<String, Object> processingMetadata;
    private Map<String, Object> processedVariants;
    private LocalDateTime expiresAt;
    private LocalDateTime archivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}