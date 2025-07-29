package com.example.mrquiz.repository.quiz;

import com.example.mrquiz.entity.quiz.Question;
import com.example.mrquiz.enums.DifficultyLevel;
import com.example.mrquiz.enums.QuestionStatus;
import com.example.mrquiz.enums.QuestionType;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends BaseRepository<Question> {
    
    // ===== BASIC QUESTION QUERIES =====
    
    /**
     * Find questions by creator
     */
    Page<Question> findByCreatorId(UUID creatorId, Pageable pageable);
    
    /**
     * Find questions by type
     */
    List<Question> findByQuestionType(QuestionType questionType);
    
    /**
     * Find questions by difficulty level
     */
    List<Question> findByDifficultyLevel(DifficultyLevel difficultyLevel);
    
    /**
     * Find questions by status
     */
    List<Question> findByStatus(QuestionStatus status);
    
    /**
     * Find question by ID and creator (security check)
     */
    Optional<Question> findByIdAndCreatorId(UUID id, UUID creatorId);
    
    // ===== TEACHER QUESTION BANK MANAGEMENT =====
    
    /**
     * Find all questions created by teacher
     */
    @Query("SELECT q FROM Question q WHERE q.creator.id = :teacherId ORDER BY q.updatedAt DESC")
    Page<Question> findTeacherQuestions(@Param("teacherId") UUID teacherId, Pageable pageable);
    
    /**
     * Find teacher's questions by type
     */
    @Query("SELECT q FROM Question q WHERE q.creator.id = :teacherId AND q.questionType = :type " +
           "ORDER BY q.updatedAt DESC")
    List<Question> findTeacherQuestionsByType(@Param("teacherId") UUID teacherId, 
                                            @Param("type") QuestionType type);
    
    /**
     * Find teacher's questions by subject area
     */
    @Query("SELECT q FROM Question q WHERE q.creator.id = :teacherId " +
           "AND :subjectArea MEMBER OF q.subjectAreas ORDER BY q.updatedAt DESC")
    List<Question> findTeacherQuestionsBySubject(@Param("teacherId") UUID teacherId, 
                                               @Param("subjectArea") String subjectArea);
    
    /**
     * Find teacher's questions by tags
     */
    @Query("SELECT q FROM Question q WHERE q.creator.id = :teacherId " +
           "AND :tag MEMBER OF q.tags ORDER BY q.updatedAt DESC")
    List<Question> findTeacherQuestionsByTag(@Param("teacherId") UUID teacherId, 
                                           @Param("tag") String tag);
    
    /**
     * Count questions by teacher and type
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.creator.id = :teacherId AND q.questionType = :type")
    Long countTeacherQuestionsByType(@Param("teacherId") UUID teacherId, @Param("type") QuestionType type);
    
    // ===== QUESTION SEARCH AND FILTERING =====
    
    /**
     * Search questions by text content
     */
    @Query("SELECT q FROM Question q WHERE " +
           "LOWER(q.questionText) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(q.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND q.status = 'ACTIVE'")
    Page<Question> searchQuestionsByText(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Advanced question search with multiple filters
     */
    @Query("SELECT q FROM Question q WHERE " +
           "(:creatorId IS NULL OR q.creator.id = :creatorId) " +
           "AND (:questionType IS NULL OR q.questionType = :questionType) " +
           "AND (:difficulty IS NULL OR q.difficultyLevel = :difficulty) " +
           "AND (:status IS NULL OR q.status = :status) " +
           "AND (:courseId IS NULL OR q.course.id = :courseId) " +
           "AND (:searchText IS NULL OR " +
           "     LOWER(q.questionText) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "     LOWER(q.title) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "ORDER BY q.updatedAt DESC")
    Page<Question> searchQuestionsWithFilters(@Param("creatorId") UUID creatorId,
                                            @Param("questionType") QuestionType questionType,
                                            @Param("difficulty") DifficultyLevel difficulty,
                                            @Param("status") QuestionStatus status,
                                            @Param("courseId") UUID courseId,
                                            @Param("searchText") String searchText,
                                            Pageable pageable);
    
    /**
     * Find questions by tags (any matching tag)
     */
    @Query("SELECT DISTINCT q FROM Question q JOIN q.tags t WHERE t IN :tags AND q.status = 'ACTIVE'")
    List<Question> findByTagsIn(@Param("tags") List<String> tags);
    
    /**
     * Find questions by multiple subject areas
     */
    @Query("SELECT DISTINCT q FROM Question q JOIN q.subjectAreas s WHERE s IN :subjects AND q.status = 'ACTIVE'")
    List<Question> findBySubjectAreasIn(@Param("subjects") List<String> subjects);
    
    // ===== QUESTION BANK ORGANIZATION =====
    
    /**
     * Find all unique tags used by teacher
     */
    @Query("SELECT DISTINCT t FROM Question q JOIN q.tags t WHERE q.creator.id = :teacherId")
    List<String> findTeacherTags(@Param("teacherId") UUID teacherId);
    
    /**
     * Find all unique subject areas used by teacher
     */
    @Query("SELECT DISTINCT s FROM Question q JOIN q.subjectAreas s WHERE q.creator.id = :teacherId")
    List<String> findTeacherSubjectAreas(@Param("teacherId") UUID teacherId);
    
    /**
     * Find questions with similar tags (for recommendations)
     */
    @Query("SELECT q FROM Question q WHERE q.id != :questionId AND q.status = 'ACTIVE' " +
           "AND EXISTS (SELECT 1 FROM Question q2 JOIN q2.tags t WHERE q2.id = :questionId " +
           "            AND t MEMBER OF q.tags)")
    List<Question> findSimilarQuestions(@Param("questionId") UUID questionId, Pageable pageable);
    
    // ===== QUESTION SHARING AND TEMPLATES =====
    
    /**
     * Find public questions available for sharing
     */
    @Query("SELECT q FROM Question q WHERE q.isPublic = true AND q.status = 'ACTIVE'")
    Page<Question> findPublicQuestions(Pageable pageable);
    
    /**
     * Find shareable questions by teacher
     */
    @Query("SELECT q FROM Question q WHERE q.creator.id = :teacherId " +
           "AND JSON_EXTRACT(q.sharingPermissions, '$.allowSharing') = true " +
           "AND q.status = 'ACTIVE'")
    List<Question> findShareableQuestions(@Param("teacherId") UUID teacherId);
    
    /**
     * Find question templates
     */
    @Query("SELECT q FROM Question q WHERE JSON_EXTRACT(q.sharingPermissions, '$.isTemplate') = true " +
           "AND q.status = 'ACTIVE'")
    List<Question> findQuestionTemplates();
    
    // ===== QUESTION ANALYTICS AND PERFORMANCE =====
    
    /**
     * Find most used questions
     */
    @Query("SELECT q FROM Question q WHERE q.usageCount > 0 ORDER BY q.usageCount DESC")
    List<Question> findMostUsedQuestions(Pageable pageable);
    
    /**
     * Find questions with performance metrics
     */
    @Query("SELECT q FROM Question q WHERE q.difficultyIndex IS NOT NULL " +
           "AND q.discriminationIndex IS NOT NULL ORDER BY q.discriminationIndex DESC")
    List<Question> findQuestionsWithMetrics();
    
    /**
     * Find questions needing review (poor performance)
     */
    @Query("SELECT q FROM Question q WHERE " +
           "(q.difficultyIndex IS NOT NULL AND q.difficultyIndex < 0.3) " +
           "OR (q.discriminationIndex IS NOT NULL AND q.discriminationIndex < 0.2) " +
           "AND q.usageCount >= :minUsage")
    List<Question> findQuestionsNeedingReview(@Param("minUsage") Integer minUsage);
    
    /**
     * Find high-quality questions (good performance metrics)
     */
    @Query("SELECT q FROM Question q WHERE q.difficultyIndex BETWEEN 0.4 AND 0.8 " +
           "AND q.discriminationIndex > 0.3 AND q.usageCount >= :minUsage")
    List<Question> findHighQualityQuestions(@Param("minUsage") Integer minUsage);
    
    // ===== QUESTION VERSIONING =====
    
    /**
     * Find question versions
     */
    @Query("SELECT q FROM Question q WHERE q.parentQuestion.id = :parentId ORDER BY q.version DESC")
    List<Question> findQuestionVersions(@Param("parentId") UUID parentId);
    
    /**
     * Find latest version of question
     */
    @Query("SELECT q FROM Question q WHERE q.parentQuestion.id = :parentId " +
           "ORDER BY q.version DESC LIMIT 1")
    Optional<Question> findLatestQuestionVersion(@Param("parentId") UUID parentId);
    
    // ===== MULTIMEDIA AND RICH CONTENT =====
    
    /**
     * Find questions with file attachments
     */
    @Query("SELECT q FROM Question q WHERE JSON_LENGTH(q.questionFiles) > 0")
    List<Question> findQuestionsWithFiles();
    
    /**
     * Find questions by media type (based on files)
     */
    @Query("SELECT q FROM Question q JOIN File f ON JSON_CONTAINS(q.questionFiles, CAST(f.id AS CHAR)) " +
           "WHERE f.fileType = :mediaType")
    List<Question> findQuestionsByMediaType(@Param("mediaType") String mediaType);
    
    /**
     * Find questions with LaTeX/mathematical content
     */
    @Query("SELECT q FROM Question q WHERE q.questionType = 'MATH' " +
           "OR JSON_EXTRACT(q.questionData, '$.hasLatex') = true")
    List<Question> findMathematicalQuestions();
    
    // ===== ACCESSIBILITY AND COMPLIANCE =====
    
    /**
     * Find questions with accessibility features
     */
    @Query("SELECT q FROM Question q WHERE q.altText IS NOT NULL " +
           "OR JSON_LENGTH(q.accessibilityMetadata) > 0")
    List<Question> findAccessibleQuestions();
    
    /**
     * Find questions needing accessibility review
     */
    @Query("SELECT q FROM Question q WHERE JSON_LENGTH(q.questionFiles) > 0 " +
           "AND q.altText IS NULL")
    List<Question> findQuestionsNeedingAccessibilityReview();
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update question usage count
     */
    @Modifying
    @Query("UPDATE Question q SET q.usageCount = q.usageCount + 1 WHERE q.id = :questionId")
    void incrementUsageCount(@Param("questionId") UUID questionId);
    
    /**
     * Update question performance metrics
     */
    @Modifying
    @Query("UPDATE Question q SET q.difficultyIndex = :difficulty, q.discriminationIndex = :discrimination " +
           "WHERE q.id = :questionId")
    void updatePerformanceMetrics(@Param("questionId") UUID questionId,
                                 @Param("difficulty") BigDecimal difficulty,
                                 @Param("discrimination") BigDecimal discrimination);
    
    /**
     * Bulk update question status
     */
    @Modifying
    @Query("UPDATE Question q SET q.status = :status WHERE q.creator.id = :teacherId AND q.id IN :questionIds")
    void bulkUpdateQuestionStatus(@Param("teacherId") UUID teacherId,
                                 @Param("questionIds") List<UUID> questionIds,
                                 @Param("status") QuestionStatus status);
    
    /**
     * Update question tags
     */
    @Modifying
    @Query("UPDATE Question q SET q.tags = :tags WHERE q.id = :questionId")
    void updateQuestionTags(@Param("questionId") UUID questionId, @Param("tags") List<String> tags);
    
    // ===== STATISTICS AND REPORTING =====
    
    /**
     * Count questions by type
     */
    @Query("SELECT q.questionType, COUNT(q) FROM Question q GROUP BY q.questionType")
    List<Object[]> countQuestionsByType();
    
    /**
     * Count questions by difficulty
     */
    @Query("SELECT q.difficultyLevel, COUNT(q) FROM Question q GROUP BY q.difficultyLevel")
    List<Object[]> countQuestionsByDifficulty();
    
    /**
     * Get question creation statistics for teacher
     */
    @Query("SELECT DATE(q.createdAt) as date, COUNT(q) as count FROM Question q " +
           "WHERE q.creator.id = :teacherId AND q.createdAt BETWEEN :start AND :end " +
           "GROUP BY DATE(q.createdAt) ORDER BY date")
    List<Object[]> getTeacherQuestionCreationStats(@Param("teacherId") UUID teacherId,
                                                  @Param("start") java.time.LocalDateTime start,
                                                  @Param("end") java.time.LocalDateTime end);
    
    /**
     * Get question type distribution for teacher
     */
    @Query("SELECT q.questionType, COUNT(q) as count FROM Question q " +
           "WHERE q.creator.id = :teacherId GROUP BY q.questionType")
    List<Object[]> getTeacherQuestionTypeDistribution(@Param("teacherId") UUID teacherId);
    
    /**
     * Get most popular tags
     */
    @Query("SELECT t as tag, COUNT(q) as usage FROM Question q JOIN q.tags t " +
           "GROUP BY t ORDER BY usage DESC")
    List<Object[]> getMostPopularTags(Pageable pageable);
    
    /**
     * Get teacher question bank summary
     */
    @Query("SELECT " +
           "COUNT(q) as totalQuestions, " +
           "COUNT(CASE WHEN q.status = 'ACTIVE' THEN 1 END) as activeQuestions, " +
           "COUNT(CASE WHEN q.isPublic = true THEN 1 END) as publicQuestions, " +
           "AVG(q.usageCount) as avgUsage, " +
           "COUNT(DISTINCT qt.questionType) as uniqueTypes " +
           "FROM Question q LEFT JOIN Question qt ON q.creator.id = qt.creator.id " +
           "WHERE q.creator.id = :teacherId")
    Object[] getTeacherQuestionBankSummary(@Param("teacherId") UUID teacherId);
}