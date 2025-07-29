package com.example.mrquiz.dto.file;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class FileVariantResponseDto {
    private UUID id;
    private UUID parentFileId;
    private String variantName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private Map<String, Object> dimensions;
    private Map<String, Object> processingMetadata;
    private LocalDateTime createdAt;
}