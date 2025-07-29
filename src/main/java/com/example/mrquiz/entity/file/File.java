package com.example.mrquiz.entity.file;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.enums.FileStatus;
import com.example.mrquiz.enums.FileType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "files", indexes = {
    @Index(name = "idx_files_uploader", columnList = "uploadedBy, createdAt"),
    @Index(name = "idx_files_status", columnList = "status"),
    @Index(name = "idx_files_hash", columnList = "fileHash"),
    @Index(name = "idx_files_type_public", columnList = "fileType, isPublic")
})
public class File extends BaseEntity {
    
    @Column(nullable = false)
    private String filename;
    
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @Column(name = "file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;
    
    @Column(name = "file_hash", nullable = false, length = 64)
    private String fileHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status = FileStatus.UPLOADING;
    
    // Storage metadata
    @Column(name = "storage_provider", length = 50)
    private String storageProvider = "local";
    
    @Column(name = "storage_region", length = 50)
    private String storageRegion;
    
    @Column(name = "storage_bucket", length = 100)
    private String storageBucket;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "storage_metadata", columnDefinition = "jsonb")
    private Map<String, Object> storageMetadata;
    
    // File metadata
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> dimensions;
    
    @Column
    private Integer duration; // For audio/video in seconds
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "encoding_info", columnDefinition = "jsonb")
    private Map<String, Object> encodingInfo;
    
    // Access control
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
    
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "access_permissions", columnDefinition = "jsonb")
    private Map<String, Object> accessPermissions;
    
    // Processing info
    @Column(name = "processing_status")
    private String processingStatus = "pending";
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "processing_metadata", columnDefinition = "jsonb")
    private Map<String, Object> processingMetadata;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "processed_variants", columnDefinition = "jsonb")
    private Map<String, Object> processedVariants;
    
    // Lifecycle
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
}