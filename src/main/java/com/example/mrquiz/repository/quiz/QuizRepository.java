package com.example.mrquiz.repository.quiz;

import com.example.mrquiz.entity.quiz.Quiz;
import com.example.mrquiz.enums.QuizStatus;
import com.example.mrquiz.enums.QuizType;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRepository extends BaseRepository<Quiz> {
    
    // ===== BASIC QUIZ QUERIES =====
    
    /**
     * Find quiz by creator
     */
    List<Quiz> findByCreatorId(UUID creatorId);
    
    /**
     * Find quizzes by creator with pagination
     */
    Page<Quiz> findByCreatorId(UUID creatorId, Pageable pageable);
    
    /**
     * Find quizzes by status
     */
    List<Quiz> findByStatus(QuizStatus status);
    
    /**
     * Find quizzes by type and status
     */
    List<Quiz> findByQuizTypeAndStatus(QuizType quizType, QuizStatus status);
    
    /**
     * Find quiz by ID and creator (security check)
     */
    Optional<Quiz> findByIdAndCreatorId(UUID id, UUID creatorId);
    
    // ===== TEACHER QUIZ MANAGEMENT =====
    
    /**
     * Find all quizzes created by teacher
     */
    @Query("SELECT q FROM Quiz q WHERE q.creator.id = :teacherId ORDER BY q.updatedAt DESC")
    Page<Quiz> findTeacherQuizzes(@Param("teacherId") UUID teacherId, Pageable pageable);
    
    /**
     * Find teacher's quizzes by status
     */
    @Query("SELECT q FROM Quiz q WHERE q.creator.id = :teacherId AND q.status = :status ORDER BY q.updatedAt DESC")
    List<Quiz> findTeacherQuizzesByStatus(@Param("teacherId") UUID teacherId, @Param("status") QuizStatus status);
    
    /**
     * Find teacher's published quizzes
     */
    @Query("SELECT q FROM Quiz q WHERE q.creator.id = :teacherId AND q.status = 'PUBLISHED' ORDER BY q.createdAt DESC")
    List<Quiz> findTeacherPublishedQuizzes(@Param("teacherId") UUID teacherId);
    
    /**
     * Find teacher's draft quizzes
     */
    @Query("SELECT q FROM Quiz q WHERE q.creator.id = :teacherId AND q.status = 'DRAFT' ORDER BY q.updatedAt DESC")
    List<Quiz> findTeacherDraftQuizzes(@Param("teacherId") UUID teacherId);
    
    /**
     * Count quizzes by teacher and status
     */
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.creator.id = :teacherId AND q.status = :status")
    Long countTeacherQuizzesByStatus(@Param("teacherId") UUID teacherId, @Param("status") QuizStatus status);
    
    // ===== QUIZ DISCOVERY AND ACCESS =====
    
    /**
     * Find available quizzes (published and within availability window)
     */
    @Query("SELECT q FROM Quiz q WHERE q.status = 'PUBLISHED' " +
           "AND (q.availabilityStart IS NULL OR q.availabilityStart <= :now) " +
           "AND (q.availabilityEnd IS NULL OR q.availabilityEnd >= :now)")
    List<Quiz> findAvailableQuizzes(@Param("now") LocalDateTime now);
    
    /**
     * Find public quizzes (no course/institution restriction)
     */
    @Query("SELECT q FROM Quiz q WHERE q.status = 'PUBLISHED' AND q.course IS NULL AND q.institution IS NULL " +
           "AND (q.availabilityStart IS NULL OR q.availabilityStart <= :now) " +
           "AND (q.availabilityEnd IS NULL OR q.availabilityEnd >= :now)")
    Page<Quiz> findPublicQuizzes(@Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Find quizzes by course
     */
    @Query("SELECT q FROM Quiz q WHERE q.course.id = :courseId AND q.status = 'PUBLISHED' ORDER BY q.createdAt DESC")
    List<Quiz> findByCourseId(@Param("courseId") UUID courseId);
    
    /**
     * Find quizzes by institution
     */
    @Query("SELECT q FROM Quiz q WHERE q.institution.id = :institutionId ORDER BY q.createdAt DESC")
    Page<Quiz> findByInstitutionId(@Param("institutionId") UUID institutionId, Pageable pageable);
    
    // ===== QUIZ SEARCH AND FILTERING =====
    
    /**
     * Search quizzes by title
     */
    @Query("SELECT q FROM Quiz q WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND q.status = 'PUBLISHED'")
    Page<Quiz> searchByTitle(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Search quizzes with multiple filters
     */
    @Query("SELECT q FROM Quiz q WHERE " +
           "(:title IS NULL OR LOWER(q.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:creatorId IS NULL OR q.creator.id = :creatorId) " +
           "AND (:quizType IS NULL OR q.quizType = :quizType) " +
           "AND (:status IS NULL OR q.status = :status) " +
           "AND (:courseId IS NULL OR q.course.id = :courseId) " +
           "ORDER BY q.updatedAt DESC")
    Page<Quiz> searchQuizzesWithFilters(@Param("title") String title,
                                       @Param("creatorId") UUID creatorId,
                                       @Param("quizType") QuizType quizType,
                                       @Param("status") QuizStatus status,
                                       @Param("courseId") UUID courseId,
                                       Pageable pageable);
    
    // ===== QUIZ TEMPLATES AND SHARING =====
    
    /**
     * Find quiz templates (marked as templates in settings)
     */
    @Query("SELECT q FROM Quiz q WHERE JSON_EXTRACT(q.settings, '$.isTemplate') = true " +
           "AND q.status = 'PUBLISHED'")
    List<Quiz> findQuizTemplates();
    
    /**
     * Find shareable quizzes by teacher
     */
    @Query("SELECT q FROM Quiz q WHERE q.creator.id = :teacherId " +
           "AND JSON_EXTRACT(q.settings, '$.allowSharing') = true " +
           "AND q.status = 'PUBLISHED'")
    List<Quiz> findShareableQuizzes(@Param("teacherId") UUID teacherId);
    
    /**
     * Find featured quizzes
     */
    @Query("SELECT q FROM Quiz q WHERE JSON_EXTRACT(q.settings, '$.featured') = true " +
           "AND q.status = 'PUBLISHED' ORDER BY q.createdAt DESC")
    List<Quiz> findFeaturedQuizzes(Pageable pageable);
    
    // ===== QUIZ SCHEDULING AND AVAILABILITY =====
    
    /**
     * Find scheduled quizzes
     */
    @Query("SELECT q FROM Quiz q WHERE q.status = 'SCHEDULED' " +
           "AND q.availabilityStart > :now ORDER BY q.availabilityStart ASC")
    List<Quiz> findScheduledQuizzes(@Param("now") LocalDateTime now);
    
    /**
     * Find quizzes starting soon
     */
    @Query("SELECT q FROM Quiz q WHERE q.status = 'SCHEDULED' " +
           "AND q.availabilityStart BETWEEN :now AND :threshold")
    List<Quiz> findQuizzesStartingSoon(@Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);
    
    /**
     * Find expired quizzes
     */
    @Query("SELECT q FROM Quiz q WHERE q.availabilityEnd IS NOT NULL " +
           "AND q.availabilityEnd < :now AND q.status = 'PUBLISHED'")
    List<Quiz> findExpiredQuizzes(@Param("now") LocalDateTime now);
    
    // ===== QUIZ ANALYTICS SUPPORT =====
    
    /**
     * Find most popular quizzes by attempt count
     */
    @Query("SELECT q, COUNT(qa) as attemptCount FROM Quiz q " +
           "LEFT JOIN QuizAttempt qa ON q.id = qa.quiz.id " +
           "WHERE q.status = 'PUBLISHED' " +
           "GROUP BY q.id ORDER BY attemptCount DESC")
    List<Object[]> findMostPopularQuizzes(Pageable pageable);
    
    /**
     * Find highest rated quizzes
     */
    @Query("SELECT q, AVG(qa.percentage) as avgScore FROM Quiz q " +
           "JOIN QuizAttempt qa ON q.id = qa.quiz.id " +
           "WHERE q.status = 'PUBLISHED' AND qa.status = 'GRADED' " +
           "GROUP BY q.id HAVING COUNT(qa) >= :minAttempts " +
           "ORDER BY avgScore DESC")
    List<Object[]> findHighestRatedQuizzes(@Param("minAttempts") Long minAttempts, Pageable pageable);
    
    /**
     * Get quiz performance summary
     */
    @Query("SELECT q.id, q.title, " +
           "COUNT(DISTINCT qa.id) as totalAttempts, " +
           "COUNT(DISTINCT qa.user.id) as uniqueParticipants, " +
           "AVG(qa.percentage) as averageScore, " +
           "COUNT(CASE WHEN qa.passed = true THEN 1 END) as passedAttempts " +
           "FROM Quiz q " +
           "LEFT JOIN QuizAttempt qa ON q.id = qa.quiz.id " +
           "WHERE q.creator.id = :teacherId " +
           "GROUP BY q.id, q.title")
    List<Object[]> getTeacherQuizPerformanceSummary(@Param("teacherId") UUID teacherId);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update quiz status
     */
    @Modifying
    @Query("UPDATE Quiz q SET q.status = :status, q.updatedAt = :updatedAt WHERE q.id = :quizId")
    void updateQuizStatus(@Param("quizId") UUID quizId, @Param("status") QuizStatus status, 
                         @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * Update quiz total points
     */
    @Modifying
    @Query("UPDATE Quiz q SET q.totalPoints = :totalPoints WHERE q.id = :quizId")
    void updateTotalPoints(@Param("quizId") UUID quizId, @Param("totalPoints") BigDecimal totalPoints);
    
    /**
     * Bulk update quiz status for teacher
     */
    @Modifying
    @Query("UPDATE Quiz q SET q.status = :status WHERE q.creator.id = :teacherId AND q.id IN :quizIds")
    void bulkUpdateQuizStatus(@Param("teacherId") UUID teacherId, 
                             @Param("quizIds") List<UUID> quizIds, 
                             @Param("status") QuizStatus status);
    
    // ===== COLLABORATION AND VERSION CONTROL =====
    
    /**
     * Find quiz versions (if versioning is implemented)
     */
    @Query("SELECT q FROM Quiz q WHERE JSON_EXTRACT(q.settings, '$.parentQuizId') = :parentQuizId " +
           "ORDER BY q.createdAt DESC")
    List<Quiz> findQuizVersions(@Param("parentQuizId") String parentQuizId);
    
    /**
     * Find collaborative quizzes where user has edit access
     */
    @Query("SELECT q FROM Quiz q WHERE q.creator.id = :userId " +
           "OR JSON_CONTAINS(q.settings, :userId, '$.collaborators')")
    List<Quiz> findCollaborativeQuizzes(@Param("userId") String userId);
    
    // ===== STUDENT ACCESS AND INVITATION =====
    
    /**
     * Find quizzes accessible to student via join codes
     */
    @Query("SELECT q FROM Quiz q WHERE JSON_EXTRACT(q.settings, '$.joinCode') = :joinCode " +
           "AND q.status = 'PUBLISHED' " +
           "AND (q.availabilityStart IS NULL OR q.availabilityStart <= :now) " +
           "AND (q.availabilityEnd IS NULL OR q.availabilityEnd >= :now)")
    Optional<Quiz> findByJoinCode(@Param("joinCode") String joinCode, @Param("now") LocalDateTime now);
    
    /**
     * Find quizzes with guest access enabled
     */
    @Query("SELECT q FROM Quiz q WHERE JSON_EXTRACT(q.settings, '$.allowGuestAccess') = true " +
           "AND q.status = 'PUBLISHED'")
    List<Quiz> findGuestAccessibleQuizzes();
    
    // ===== ADVANCED QUIZ FEATURES =====
    
    /**
     * Find adaptive quizzes
     */
    @Query("SELECT q FROM Quiz q WHERE JSON_EXTRACT(q.settings, '$.adaptiveTesting') = true " +
           "AND q.status = 'PUBLISHED'")
    List<Quiz> findAdaptiveQuizzes();
    
    /**
     * Find proctored quizzes
     */
    @Query("SELECT q FROM Quiz q WHERE JSON_LENGTH(q.proctoringSettings) > 0 " +
           "AND q.status = 'PUBLISHED'")
    List<Quiz> findProctoredQuizzes();
    
    /**
     * Find timed quizzes
     */
    @Query("SELECT q FROM Quiz q WHERE q.timeLimit IS NOT NULL AND q.timeLimit > 0 " +
           "AND q.status = 'PUBLISHED'")
    List<Quiz> findTimedQuizzes();
    
    // ===== STATISTICS AND REPORTING =====
    
    /**
     * Count quizzes by type
     */
    @Query("SELECT q.quizType, COUNT(q) FROM Quiz q GROUP BY q.quizType")
    List<Object[]> countQuizzesByType();
    
    /**
     * Count quizzes by status
     */
    @Query("SELECT q.status, COUNT(q) FROM Quiz q GROUP BY q.status")
    List<Object[]> countQuizzesByStatus();
    
    /**
     * Get quiz creation statistics for period
     */
    @Query("SELECT DATE(q.createdAt) as date, COUNT(q) as count FROM Quiz q " +
           "WHERE q.createdAt BETWEEN :start AND :end " +
           "GROUP BY DATE(q.createdAt) ORDER BY date")
    List<Object[]> getQuizCreationStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * Get teacher productivity statistics
     */
    @Query("SELECT q.creator.id, " +
           "CONCAT(q.creator.firstName, ' ', q.creator.lastName) as teacherName, " +
           "COUNT(q) as totalQuizzes, " +
           "COUNT(CASE WHEN q.status = 'PUBLISHED' THEN 1 END) as publishedQuizzes, " +
           "COUNT(CASE WHEN q.status = 'DRAFT' THEN 1 END) as draftQuizzes " +
           "FROM Quiz q " +
           "WHERE q.creator.role = 'TEACHER' " +
           "GROUP BY q.creator.id, q.creator.firstName, q.creator.lastName " +
           "ORDER BY totalQuizzes DESC")
    List<Object[]> getTeacherProductivityStats();
}