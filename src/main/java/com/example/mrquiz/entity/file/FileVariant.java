package com.example.mrquiz.entity.file;

import com.example.mrquiz.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "file_variants", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"parent_file_id", "variant_name"}),
       indexes = @Index(name = "idx_file_variants_parent", columnList = "parent_file_id"))
public class FileVariant extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_file_id", nullable = false)
    private File parentFile;
    
    @Column(name = "variant_name", nullable = false, length = 100)
    private String variantName; // 'thumbnail', 'small', 'medium', 'webp', etc.
    
    @Column(name = "file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> dimensions;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "processing_metadata", columnDefinition = "jsonb")
    private Map<String, Object> processingMetadata;
}