package com.example.mrquiz.repository.file;

import com.example.mrquiz.entity.file.File;
import com.example.mrquiz.enums.FileStatus;
import com.example.mrquiz.enums.FileType;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends BaseRepository<File> {
    
    // ===== BASIC FILE QUERIES =====
    
    /**
     * Find file by hash and uploader (for deduplication)
     */
    Optional<File> findByFileHashAndUploadedById(String fileHash, UUID uploadedById);
    
    /**
     * Find files by uploader
     */
    Page<File> findByUploadedById(UUID uploadedById, Pageable pageable);
    
    /**
     * Find files by type
     */
    List<File> findByFileType(FileType fileType);
    
    /**
     * Find files by status
     */
    List<File> findByStatus(FileStatus status);
    
    /**
     * Find files by MIME type
     */
    List<File> findByMimeType(String mimeType);
    
    /**
     * Find public files
     */
    @Query("SELECT f FROM File f WHERE f.isPublic = true AND f.status = 'READY' ORDER BY f.createdAt DESC")
    Page<File> findPublicFiles(Pageable pageable);
    
    // ===== FILE STATUS MANAGEMENT =====
    
    /**
     * Find files being processed
     */
    @Query("SELECT f FROM File f WHERE f.status IN ('UPLOADING', 'PROCESSING') ORDER BY f.createdAt ASC")
    List<File> findFilesBeingProcessed();
    
    /**
     * Find ready files
     */
    @Query("SELECT f FROM File f WHERE f.status = 'READY' ORDER BY f.createdAt DESC")
    Page<File> findReadyFiles(Pageable pageable);
    
    /**
     * Find failed files
     */
    @Query("SELECT f FROM File f WHERE f.status = 'FAILED' ORDER BY f.createdAt DESC")
    List<File> findFailedFiles();
    
    /**
     * Find archived files
     */
    @Query("SELECT f FROM File f WHERE f.status = 'ARCHIVED' ORDER BY f.archivedAt DESC")
    Page<File> findArchivedFiles(Pageable pageable);
    
    // ===== MULTIMEDIA FILE QUERIES =====
    
    /**
     * Find image files
     */
    @Query("SELECT f FROM File f WHERE f.fileType = 'IMAGE' AND f.status = 'READY' ORDER BY f.createdAt DESC")
    List<File> findImageFiles();
    
    /**
     * Find video files
     */
    @Query("SELECT f FROM File f WHERE f.fileType = 'VIDEO' AND f.status = 'READY' ORDER BY f.createdAt DESC")
    List<File> findVideoFiles();
    
    /**
     * Find audio files
     */
    @Query("SELECT f FROM File f WHERE f.fileType = 'AUDIO' AND f.status = 'READY' ORDER BY f.createdAt DESC")
    List<File> findAudioFiles();
    
    /**
     * Find document files
     */
    @Query("SELECT f FROM File f WHERE f.fileType = 'DOCUMENT' AND f.status = 'READY' ORDER BY f.createdAt DESC")
    List<File> findDocumentFiles();
    
    /**
     * Find files with dimensions (images/videos)
     */
    @Query("SELECT f FROM File f WHERE JSON_LENGTH(f.dimensions) > 0 AND f.status = 'READY'")
    List<File> findFilesWithDimensions();
    
    /**
     * Find files by dimension range
     */
    @Query("SELECT f FROM File f WHERE f.fileType IN ('IMAGE', 'VIDEO') " +
           "AND CAST(JSON_EXTRACT(f.dimensions, '$.width') AS INTEGER) BETWEEN :minWidth AND :maxWidth " +
           "AND CAST(JSON_EXTRACT(f.dimensions, '$.height') AS INTEGER) BETWEEN :minHeight AND :maxHeight " +
           "AND f.status = 'READY'")
    List<File> findFilesByDimensions(@Param("minWidth") Integer minWidth, @Param("maxWidth") Integer maxWidth,
                                    @Param("minHeight") Integer minHeight, @Param("maxHeight") Integer maxHeight);
    
    // ===== STORAGE AND SIZE MANAGEMENT =====
    
    /**
     * Find large files
     */
    @Query("SELECT f FROM File f WHERE f.fileSize > :sizeThreshold ORDER BY f.fileSize DESC")
    List<File> findLargeFiles(@Param("sizeThreshold") Long sizeThreshold);
    
    /**
     * Find files by storage provider
     */
    @Query("SELECT f FROM File f WHERE f.storageProvider = :provider ORDER BY f.createdAt DESC")
    List<File> findByStorageProvider(@Param("provider") String provider);
    
    /**
     * Calculate total storage used by user
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM File f WHERE f.uploadedBy.id = :userId AND f.status != 'FAILED'")
    Long calculateUserStorageUsage(@Param("userId") UUID userId);
    
    /**
     * Calculate total storage by file type
     */
    @Query("SELECT f.fileType, COALESCE(SUM(f.fileSize), 0) FROM File f " +
           "WHERE f.status != 'FAILED' GROUP BY f.fileType")
    List<Object[]> calculateStorageByFileType();
    
    /**
     * Find files exceeding size limit
     */
    @Query("SELECT f FROM File f WHERE f.fileSize > :sizeLimit ORDER BY f.fileSize DESC")
    List<File> findFilesExceedingSizeLimit(@Param("sizeLimit") Long sizeLimit);
    
    // ===== ACCESS CONTROL =====
    
    /**
     * Find files accessible to user
     */
    @Query("SELECT f FROM File f WHERE f.isPublic = true OR f.uploadedBy.id = :userId " +
           "OR JSON_EXTRACT(f.accessPermissions, CONCAT('$.users.', :userId)) IS NOT NULL " +
           "AND f.status = 'READY' ORDER BY f.createdAt DESC")
    Page<File> findAccessibleFiles(@Param("userId") UUID userId, Pageable pageable);
    
    /**
     * Find files with specific access permission
     */
    @Query("SELECT f FROM File f WHERE JSON_EXTRACT(f.accessPermissions, CONCAT('$.', :permission)) = true " +
           "AND f.status = 'READY'")
    List<File> findFilesWithPermission(@Param("permission") String permission);
    
    /**
     * Find private files by user
     */
    @Query("SELECT f FROM File f WHERE f.uploadedBy.id = :userId AND f.isPublic = false " +
           "AND f.status = 'READY' ORDER BY f.createdAt DESC")
    List<File> findPrivateFilesByUser(@Param("userId") UUID userId);
    
    // ===== FILE PROCESSING =====
    
    /**
     * Find files needing processing
     */
    @Query("SELECT f FROM File f WHERE f.processingStatus = 'PENDING' AND f.status = 'READY' " +
           "ORDER BY f.createdAt ASC")
    List<File> findFilesNeedingProcessing();
    
    /**
     * Find files currently being processed
     */
    @Query("SELECT f FROM File f WHERE f.processingStatus = 'PROCESSING' ORDER BY f.createdAt ASC")
    List<File> findFilesCurrentlyProcessing();
    
    /**
     * Find files with failed processing
     */
    @Query("SELECT f FROM File f WHERE f.processingStatus = 'FAILED' ORDER BY f.createdAt DESC")
    List<File> findFilesWithFailedProcessing();
    
    /**
     * Find files with completed processing
     */
    @Query("SELECT f FROM File f WHERE f.processingStatus = 'COMPLETED' AND f.status = 'READY' " +
           "ORDER BY f.createdAt DESC")
    List<File> findFilesWithCompletedProcessing();
    
    // ===== LIFECYCLE MANAGEMENT =====
    
    /**
     * Find expired files
     */
    @Query("SELECT f FROM File f WHERE f.expiresAt IS NOT NULL AND f.expiresAt <= :now " +
           "AND f.status != 'ARCHIVED'")
    List<File> findExpiredFiles(@Param("now") LocalDateTime now);
    
    /**
     * Find files expiring soon
     */
    @Query("SELECT f FROM File f WHERE f.expiresAt IS NOT NULL " +
           "AND f.expiresAt BETWEEN :now AND :threshold AND f.status = 'READY'")
    List<File> findFilesExpiringSoon(@Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);
    
    /**
     * Find old files for cleanup
     */
    @Query("SELECT f FROM File f WHERE f.createdAt < :cutoffDate AND f.status != 'ARCHIVED'")
    List<File> findOldFiles(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find unused files (no references)
     */
    @Query("SELECT f FROM File f WHERE f.status = 'READY' " +
           "AND NOT EXISTS (SELECT 1 FROM User u WHERE u.profileImage.id = f.id) " +
           "AND NOT EXISTS (SELECT 1 FROM Question q WHERE f.id MEMBER OF q.questionFiles) " +
           "AND NOT EXISTS (SELECT 1 FROM ResponseFile rf WHERE rf.file.id = f.id)")
    List<File> findUnusedFiles();
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search files by filename
     */
    @Query("SELECT f FROM File f WHERE LOWER(f.filename) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND f.status = 'READY' ORDER BY f.createdAt DESC")
    Page<File> searchByFilename(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Search files by original filename
     */
    @Query("SELECT f FROM File f WHERE LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND f.status = 'READY' ORDER BY f.createdAt DESC")
    Page<File> searchByOriginalFilename(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Advanced file search with filters
     */
    @Query("SELECT f FROM File f WHERE " +
           "(:uploaderId IS NULL OR f.uploadedBy.id = :uploaderId) " +
           "AND (:fileType IS NULL OR f.fileType = :fileType) " +
           "AND (:status IS NULL OR f.status = :status) " +
           "AND (:mimeType IS NULL OR f.mimeType = :mimeType) " +
           "AND (:isPublic IS NULL OR f.isPublic = :isPublic) " +
           "AND (:filename IS NULL OR LOWER(f.filename) LIKE LOWER(CONCAT('%', :filename, '%'))) " +
           "ORDER BY f.createdAt DESC")
    Page<File> searchFilesWithFilters(@Param("uploaderId") UUID uploaderId,
                                     @Param("fileType") FileType fileType,
                                     @Param("status") FileStatus status,
                                     @Param("mimeType") String mimeType,
                                     @Param("isPublic") Boolean isPublic,
                                     @Param("filename") String filename,
                                     Pageable pageable);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update file status
     */
    @Modifying
    @Query("UPDATE File f SET f.status = :status WHERE f.id = :fileId")
    void updateFileStatus(@Param("fileId") UUID fileId, @Param("status") FileStatus status);
    
    /**
     * Update processing status
     */
    @Modifying
    @Query("UPDATE File f SET f.processingStatus = :processingStatus, " +
           "f.processingMetadata = :metadata WHERE f.id = :fileId")
    void updateProcessingStatus(@Param("fileId") UUID fileId, 
                               @Param("processingStatus") String processingStatus,
                               @Param("metadata") String metadata);
    
    /**
     * Archive file
     */
    @Modifying
    @Query("UPDATE File f SET f.status = 'ARCHIVED', f.archivedAt = :archivedAt WHERE f.id = :fileId")
    void archiveFile(@Param("fileId") UUID fileId, @Param("archivedAt") LocalDateTime archivedAt);
    
    /**
     * Bulk archive files
     */
    @Modifying
    @Query("UPDATE File f SET f.status = 'ARCHIVED', f.archivedAt = :archivedAt WHERE f.id IN :fileIds")
    void bulkArchiveFiles(@Param("fileIds") List<UUID> fileIds, @Param("archivedAt") LocalDateTime archivedAt);
    
    /**
     * Update file access permissions
     */
    @Modifying
    @Query("UPDATE File f SET f.accessPermissions = :permissions WHERE f.id = :fileId")
    void updateAccessPermissions(@Param("fileId") UUID fileId, @Param("permissions") String permissions);
    
    /**
     * Make file public
     */
    @Modifying
    @Query("UPDATE File f SET f.isPublic = true WHERE f.id = :fileId")
    void makeFilePublic(@Param("fileId") UUID fileId);
    
    /**
     * Make file private
     */
    @Modifying
    @Query("UPDATE File f SET f.isPublic = false WHERE f.id = :fileId")
    void makeFilePrivate(@Param("fileId") UUID fileId);
    
    // ===== ANALYTICS AND REPORTING =====
    
    /**
     * Count files by type
     */
    @Query("SELECT f.fileType, COUNT(f) FROM File f WHERE f.status != 'FAILED' GROUP BY f.fileType")
    List<Object[]> countFilesByType();
    
    /**
     * Count files by status
     */
    @Query("SELECT f.status, COUNT(f) FROM File f GROUP BY f.status")
    List<Object[]> countFilesByStatus();
    
    /**
     * Count files by user
     */
    @Query("SELECT u.email, COUNT(f) as fileCount, SUM(f.fileSize) as totalSize " +
           "FROM File f JOIN User u ON f.uploadedBy.id = u.id " +
           "WHERE f.status != 'FAILED' GROUP BY u.id, u.email ORDER BY fileCount DESC")
    List<Object[]> countFilesByUser(Pageable pageable);
    
    /**
     * Get file upload statistics for period
     */
    @Query("SELECT DATE(f.createdAt) as date, COUNT(f) as uploads, SUM(f.fileSize) as totalSize " +
           "FROM File f WHERE f.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(f.createdAt) ORDER BY date")
    List<Object[]> getUploadStatistics(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get storage usage by provider
     */
    @Query("SELECT f.storageProvider, COUNT(f) as fileCount, SUM(f.fileSize) as totalSize " +
           "FROM File f WHERE f.status != 'FAILED' GROUP BY f.storageProvider")
    List<Object[]> getStorageUsageByProvider();
    
    /**
     * Get most accessed files
     */
    @Query("SELECT f, COUNT(fal) as accessCount FROM File f " +
           "LEFT JOIN FileAccessLog fal ON f.id = fal.file.id " +
           "WHERE f.status = 'READY' GROUP BY f.id ORDER BY accessCount DESC")
    List<Object[]> getMostAccessedFiles(Pageable pageable);
    
    // ===== DEDUPLICATION =====
    
    /**
     * Find duplicate files by hash
     */
    @Query("SELECT f.fileHash, COUNT(f) as duplicateCount FROM File f " +
           "WHERE f.status != 'FAILED' GROUP BY f.fileHash HAVING COUNT(f) > 1")
    List<Object[]> findDuplicateFiles();
    
    /**
     * Find files with same hash
     */
    @Query("SELECT f FROM File f WHERE f.fileHash = :fileHash AND f.status != 'FAILED' " +
           "ORDER BY f.createdAt ASC")
    List<File> findFilesByHash(@Param("fileHash") String fileHash);
    
    /**
     * Get deduplication statistics
     */
    @Query("SELECT COUNT(DISTINCT f.fileHash) as uniqueFiles, COUNT(f) as totalFiles, " +
           "SUM(f.fileSize) as totalSize, " +
           "SUM(f.fileSize) - SUM(CASE WHEN rn = 1 THEN f.fileSize ELSE 0 END) as savedSpace " +
           "FROM (SELECT f.*, ROW_NUMBER() OVER (PARTITION BY f.fileHash ORDER BY f.createdAt) as rn " +
           "      FROM File f WHERE f.status != 'FAILED') f")
    Object[] getDeduplicationStatistics();
}