package com.example.mrquiz.repository.file;

import com.example.mrquiz.entity.file.FileVariant;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileVariantRepository extends BaseRepository<FileVariant> {
    
    // ===== BASIC VARIANT QUERIES =====
    
    /**
     * Find variants by parent file
     */
    List<FileVariant> findByParentFileId(UUID parentFileId);
    
    /**
     * Find variant by parent file and variant name
     */
    Optional<FileVariant> findByParentFileIdAndVariantName(UUID parentFileId, String variantName);
    
    /**
     * Find variants by variant name
     */
    List<FileVariant> findByVariantName(String variantName);
    
    /**
     * Check if variant exists
     */
    boolean existsByParentFileIdAndVariantName(UUID parentFileId, String variantName);
    
    // ===== THUMBNAIL MANAGEMENT =====
    
    /**
     * Find thumbnail variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.variantName LIKE '%thumbnail%' ORDER BY fv.createdAt DESC")
    List<FileVariant> findThumbnailVariants();
    
    /**
     * Find thumbnail for file
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.parentFile.id = :parentFileId " +
           "AND fv.variantName = 'thumbnail' ORDER BY fv.createdAt DESC LIMIT 1")
    Optional<FileVariant> findThumbnailForFile(@Param("parentFileId") UUID parentFileId);
    
    /**
     * Find small variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.variantName = 'small' ORDER BY fv.createdAt DESC")
    List<FileVariant> findSmallVariants();
    
    /**
     * Find medium variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.variantName = 'medium' ORDER BY fv.createdAt DESC")
    List<FileVariant> findMediumVariants();
    
    /**
     * Find large variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.variantName = 'large' ORDER BY fv.createdAt DESC")
    List<FileVariant> findLargeVariants();
    
    // ===== FORMAT CONVERSION VARIANTS =====
    
    /**
     * Find WebP variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.variantName LIKE '%webp%' OR fv.mimeType = 'image/webp' " +
           "ORDER BY fv.createdAt DESC")
    List<FileVariant> findWebPVariants();
    
    /**
     * Find AVIF variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.variantName LIKE '%avif%' OR fv.mimeType = 'image/avif' " +
           "ORDER BY fv.createdAt DESC")
    List<FileVariant> findAvifVariants();
    
    /**
     * Find compressed variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.variantName LIKE '%compressed%' " +
           "ORDER BY fv.createdAt DESC")
    List<FileVariant> findCompressedVariants();
    
    /**
     * Find variants by MIME type
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.mimeType = :mimeType ORDER BY fv.createdAt DESC")
    List<FileVariant> findByMimeType(@Param("mimeType") String mimeType);
    
    // ===== SIZE-BASED QUERIES =====
    
    /**
     * Find variants by file size range
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.fileSize BETWEEN :minSize AND :maxSize " +
           "ORDER BY fv.fileSize ASC")
    List<FileVariant> findByFileSizeRange(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);
    
    /**
     * Find variants with dimensions
     */
    @Query("SELECT fv FROM FileVariant fv WHERE JSON_LENGTH(fv.dimensions) > 0 ORDER BY fv.createdAt DESC")
    List<FileVariant> findVariantsWithDimensions();
    
    /**
     * Find variants by dimension range
     */
    @Query("SELECT fv FROM FileVariant fv WHERE JSON_LENGTH(fv.dimensions) > 0 " +
           "AND CAST(JSON_EXTRACT(fv.dimensions, '$.width') AS INTEGER) BETWEEN :minWidth AND :maxWidth " +
           "AND CAST(JSON_EXTRACT(fv.dimensions, '$.height') AS INTEGER) BETWEEN :minHeight AND :maxHeight")
    List<FileVariant> findByDimensionRange(@Param("minWidth") Integer minWidth, @Param("maxWidth") Integer maxWidth,
                                          @Param("minHeight") Integer minHeight, @Param("maxHeight") Integer maxHeight);
    
    /**
     * Find square variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE JSON_LENGTH(fv.dimensions) > 0 " +
           "AND JSON_EXTRACT(fv.dimensions, '$.width') = JSON_EXTRACT(fv.dimensions, '$.height')")
    List<FileVariant> findSquareVariants();
    
    // ===== OPTIMIZATION AND PROCESSING =====
    
    /**
     * Find variants needing optimization
     */
    @Query("SELECT fv FROM FileVariant fv WHERE JSON_EXTRACT(fv.processingMetadata, '$.needsOptimization') = true")
    List<FileVariant> findVariantsNeedingOptimization();
    
    /**
     * Find variants with processing errors
     */
    @Query("SELECT fv FROM FileVariant fv WHERE JSON_EXTRACT(fv.processingMetadata, '$.hasError') = true")
    List<FileVariant> findVariantsWithProcessingErrors();
    
    /**
     * Find variants by quality level
     */
    @Query("SELECT fv FROM FileVariant fv WHERE " +
           "CAST(JSON_EXTRACT(fv.processingMetadata, '$.quality') AS INTEGER) = :quality")
    List<FileVariant> findByQualityLevel(@Param("quality") Integer quality);
    
    /**
     * Find high-quality variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE " +
           "CAST(JSON_EXTRACT(fv.processingMetadata, '$.quality') AS INTEGER) >= 80")
    List<FileVariant> findHighQualityVariants();
    
    // ===== STORAGE OPTIMIZATION =====
    
    /**
     * Find largest variants for cleanup
     */
    @Query("SELECT fv FROM FileVariant fv ORDER BY fv.fileSize DESC")
    List<FileVariant> findLargestVariants();
    
    /**
     * Calculate total storage used by variants
     */
    @Query("SELECT COALESCE(SUM(fv.fileSize), 0) FROM FileVariant fv")
    Long calculateTotalVariantStorage();
    
    /**
     * Calculate storage by variant type
     */
    @Query("SELECT fv.variantName, COUNT(fv) as count, SUM(fv.fileSize) as totalSize " +
           "FROM FileVariant fv GROUP BY fv.variantName ORDER BY totalSize DESC")
    List<Object[]> calculateStorageByVariantType();
    
    /**
     * Find redundant variants (same dimensions and format)
     */
    @Query("SELECT fv.dimensions, fv.mimeType, COUNT(fv) as count FROM FileVariant fv " +
           "WHERE JSON_LENGTH(fv.dimensions) > 0 " +
           "GROUP BY fv.dimensions, fv.mimeType HAVING COUNT(fv) > 1")
    List<Object[]> findRedundantVariants();
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Delete variants by parent file
     */
    @Modifying
    @Query("DELETE FROM FileVariant fv WHERE fv.parentFile.id = :parentFileId")
    void deleteByParentFileId(@Param("parentFileId") UUID parentFileId);
    
    /**
     * Delete variants by variant name
     */
    @Modifying
    @Query("DELETE FROM FileVariant fv WHERE fv.variantName = :variantName")
    void deleteByVariantName(@Param("variantName") String variantName);
    
    /**
     * Delete variants larger than size threshold
     */
    @Modifying
    @Query("DELETE FROM FileVariant fv WHERE fv.fileSize > :sizeThreshold")
    void deleteVariantsLargerThan(@Param("sizeThreshold") Long sizeThreshold);
    
    /**
     * Update processing metadata
     */
    @Modifying
    @Query("UPDATE FileVariant fv SET fv.processingMetadata = :metadata WHERE fv.id = :variantId")
    void updateProcessingMetadata(@Param("variantId") UUID variantId, @Param("metadata") String metadata);
    
    // ===== ANALYTICS AND REPORTING =====
    
    /**
     * Count variants by type
     */
    @Query("SELECT fv.variantName, COUNT(fv) FROM FileVariant fv GROUP BY fv.variantName ORDER BY COUNT(fv) DESC")
    List<Object[]> countVariantsByType();
    
    /**
     * Count variants by MIME type
     */
    @Query("SELECT fv.mimeType, COUNT(fv) FROM FileVariant fv GROUP BY fv.mimeType ORDER BY COUNT(fv) DESC")
    List<Object[]> countVariantsByMimeType();
    
    /**
     * Get variant statistics
     */
    @Query("SELECT COUNT(fv) as totalVariants, " +
           "COUNT(DISTINCT fv.parentFile.id) as filesWithVariants, " +
           "AVG(fv.fileSize) as avgVariantSize, " +
           "SUM(fv.fileSize) as totalVariantStorage " +
           "FROM FileVariant fv")
    Object[] getVariantStatistics();
    
    /**
     * Get compression statistics
     */
    @Query("SELECT fv.variantName, " +
           "AVG(fv.fileSize) as avgSize, " +
           "AVG(CAST(JSON_EXTRACT(fv.processingMetadata, '$.compressionRatio') AS DECIMAL)) as avgCompression " +
           "FROM FileVariant fv WHERE JSON_EXTRACT(fv.processingMetadata, '$.compressionRatio') IS NOT NULL " +
           "GROUP BY fv.variantName")
    List<Object[]> getCompressionStatistics();
    
    // ===== SPECIALIZED QUERIES =====
    
    /**
     * Find responsive image variants for file
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.parentFile.id = :parentFileId " +
           "AND fv.variantName IN ('small', 'medium', 'large', 'thumbnail') " +
           "ORDER BY fv.fileSize ASC")
    List<FileVariant> findResponsiveVariants(@Param("parentFileId") UUID parentFileId);
    
    /**
     * Find modern format variants
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.mimeType IN ('image/webp', 'image/avif') " +
           "ORDER BY fv.createdAt DESC")
    List<FileVariant> findModernFormatVariants();
    
    /**
     * Find variants for specific parent files
     */
    @Query("SELECT fv FROM FileVariant fv WHERE fv.parentFile.id IN :parentFileIds " +
           "ORDER BY fv.parentFile.id, fv.variantName")
    List<FileVariant> findVariantsByParentFiles(@Param("parentFileIds") List<UUID> parentFileIds);
    
    /**
     * Find orphaned variants (parent file deleted)
     */
    @Query("SELECT fv FROM FileVariant fv WHERE NOT EXISTS " +
           "(SELECT 1 FROM File f WHERE f.id = fv.parentFile.id)")
    List<FileVariant> findOrphanedVariants();
}