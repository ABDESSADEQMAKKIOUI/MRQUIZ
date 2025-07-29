package com.example.mrquiz.repository.quiz;

import com.example.mrquiz.entity.quiz.QuizSession;
import com.example.mrquiz.enums.SessionStatus;
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

public interface QuizSessionRepository extends BaseRepository<QuizSession> {
    
    // ===== BASIC SESSION QUERIES =====
    
    /**
     * Find session by session code
     */
    Optional<QuizSession> findBySessionCode(String sessionCode);
    
    /**
     * Find sessions by quiz
     */
    List<QuizSession> findByQuizId(UUID quizId);
    
    /**
     * Find sessions by host
     */
    List<QuizSession> findByHostId(UUID hostId);
    
    /**
     * Find sessions by status
     */
    List<QuizSession> findByStatus(SessionStatus status);
    
    /**
     * Check if session code exists
     */
    boolean existsBySessionCode(String sessionCode);
    
    // ===== ACTIVE SESSION MANAGEMENT =====
    
    /**
     * Find active sessions
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'ACTIVE' ORDER BY qs.actualStart DESC")
    List<QuizSession> findActiveSessions();
    
    /**
     * Find active sessions by host
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.host.id = :hostId AND qs.status = 'ACTIVE' " +
           "ORDER BY qs.actualStart DESC")
    List<QuizSession> findActiveSessionsByHost(@Param("hostId") UUID hostId);
    
    /**
     * Find sessions available for joining
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status IN ('SCHEDULED', 'ACTIVE') " +
           "AND (qs.allowLateJoin = true OR qs.status = 'SCHEDULED') " +
           "AND (qs.maxParticipants IS NULL OR " +
           "     (SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.session.id = qs.id) < qs.maxParticipants) " +
           "ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findJoinableSessions();
    
    /**
     * Find sessions by join code
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.sessionCode = :joinCode " +
           "AND qs.status IN ('SCHEDULED', 'ACTIVE') " +
           "AND (qs.allowLateJoin = true OR qs.status = 'SCHEDULED')")
    Optional<QuizSession> findJoinableByCode(@Param("joinCode") String joinCode);
    
    // ===== SCHEDULED SESSION MANAGEMENT =====
    
    /**
     * Find scheduled sessions
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'SCHEDULED' " +
           "ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findScheduledSessions();
    
    /**
     * Find sessions starting soon
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'SCHEDULED' " +
           "AND qs.scheduledStart BETWEEN :now AND :threshold ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findSessionsStartingSoon(@Param("now") LocalDateTime now, 
                                              @Param("threshold") LocalDateTime threshold);
    
    /**
     * Find overdue scheduled sessions
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'SCHEDULED' " +
           "AND qs.scheduledStart < :now ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findOverdueScheduledSessions(@Param("now") LocalDateTime now);
    
    /**
     * Find sessions scheduled for today
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'SCHEDULED' " +
           "AND DATE(qs.scheduledStart) = CURRENT_DATE ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findTodaysScheduledSessions();
    
    // ===== LIVE SESSION FEATURES =====
    
    /**
     * Find sessions with real-time results enabled
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.realTimeResults = true " +
           "AND qs.status = 'ACTIVE' ORDER BY qs.actualStart DESC")
    List<QuizSession> findSessionsWithRealTimeResults();
    
    /**
     * Find sessions with leaderboard enabled
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.showLeaderboard = true " +
           "AND qs.status = 'ACTIVE' ORDER BY qs.actualStart DESC")
    List<QuizSession> findSessionsWithLeaderboard();
    
    /**
     * Find sessions allowing late join
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.allowLateJoin = true " +
           "AND qs.status = 'ACTIVE' ORDER BY qs.actualStart DESC")
    List<QuizSession> findSessionsAllowingLateJoin();
    
    /**
     * Find sessions requiring registration
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.requireRegistration = true " +
           "AND qs.status IN ('SCHEDULED', 'ACTIVE') ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findSessionsRequiringRegistration();
    
    // ===== PARTICIPATION MANAGEMENT =====
    
    /**
     * Find sessions with available spots
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status IN ('SCHEDULED', 'ACTIVE') " +
           "AND qs.maxParticipants IS NOT NULL " +
           "AND (SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.session.id = qs.id) < qs.maxParticipants " +
           "ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findSessionsWithAvailableSpots();
    
    /**
     * Find sessions at capacity
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.maxParticipants IS NOT NULL " +
           "AND (SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.session.id = qs.id) >= qs.maxParticipants")
    List<QuizSession> findSessionsAtCapacity();
    
    /**
     * Get session participation statistics
     */
    @Query("SELECT qs.id, qs.name, qs.sessionCode, " +
           "COUNT(qa) as totalParticipants, " +
           "COUNT(CASE WHEN qa.status = 'IN_PROGRESS' THEN 1 END) as activeParticipants, " +
           "COUNT(CASE WHEN qa.status = 'SUBMITTED' THEN 1 END) as completedParticipants " +
           "FROM QuizSession qs LEFT JOIN QuizAttempt qa ON qs.id = qa.session.id " +
           "WHERE qs.host.id = :hostId " +
           "GROUP BY qs.id, qs.name, qs.sessionCode ORDER BY totalParticipants DESC")
    List<Object[]> getSessionParticipationStats(@Param("hostId") UUID hostId);
    
    // ===== HOST MANAGEMENT =====
    
    /**
     * Find host's recent sessions
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.host.id = :hostId " +
           "ORDER BY qs.createdAt DESC")
    Page<QuizSession> findHostRecentSessions(@Param("hostId") UUID hostId, Pageable pageable);
    
    /**
     * Find host's active sessions
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.host.id = :hostId " +
           "AND qs.status IN ('SCHEDULED', 'ACTIVE') ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findHostActiveSessions(@Param("hostId") UUID hostId);
    
    /**
     * Count host's sessions by status
     */
    @Query("SELECT qs.status, COUNT(qs) FROM QuizSession qs WHERE qs.host.id = :hostId " +
           "GROUP BY qs.status")
    List<Object[]> countHostSessionsByStatus(@Param("hostId") UUID hostId);
    
    /**
     * Find host's most popular sessions
     */
    @Query("SELECT qs.id, qs.name, COUNT(qa) as participantCount FROM QuizSession qs " +
           "LEFT JOIN QuizAttempt qa ON qs.id = qa.session.id " +
           "WHERE qs.host.id = :hostId " +
           "GROUP BY qs.id, qs.name ORDER BY participantCount DESC")
    List<Object[]> findHostPopularSessions(@Param("hostId") UUID hostId, Pageable pageable);
    
    // ===== QUIZ-SPECIFIC SESSIONS =====
    
    /**
     * Find sessions for specific quiz
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.quiz.id = :quizId " +
           "ORDER BY qs.createdAt DESC")
    List<QuizSession> findSessionsForQuiz(@Param("quizId") UUID quizId);
    
    /**
     * Find active sessions for quiz
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.quiz.id = :quizId " +
           "AND qs.status = 'ACTIVE' ORDER BY qs.actualStart DESC")
    List<QuizSession> findActiveSessionsForQuiz(@Param("quizId") UUID quizId);
    
    /**
     * Count sessions per quiz
     */
    @Query("SELECT q.title, COUNT(qs) as sessionCount FROM QuizSession qs " +
           "JOIN Quiz q ON qs.quiz.id = q.id " +
           "WHERE qs.host.id = :hostId " +
           "GROUP BY q.id, q.title ORDER BY sessionCount DESC")
    List<Object[]> countSessionsPerQuiz(@Param("hostId") UUID hostId);
    
    // ===== TIME-BASED QUERIES =====
    
    /**
     * Find sessions by date range
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.scheduledStart BETWEEN :startDate AND :endDate " +
           "ORDER BY qs.scheduledStart ASC")
    List<QuizSession> findSessionsByDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find completed sessions
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'COMPLETED' " +
           "ORDER BY qs.actualEnd DESC")
    Page<QuizSession> findCompletedSessions(Pageable pageable);
    
    /**
     * Find long-running sessions
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'ACTIVE' " +
           "AND qs.actualStart < :threshold ORDER BY qs.actualStart ASC")
    List<QuizSession> findLongRunningSessions(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Find cancelled sessions
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'CANCELLED' " +
           "ORDER BY qs.createdAt DESC")
    List<QuizSession> findCancelledSessions();
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update session status
     */
    @Modifying
    @Query("UPDATE QuizSession qs SET qs.status = :status WHERE qs.id = :sessionId")
    void updateSessionStatus(@Param("sessionId") UUID sessionId, @Param("status") SessionStatus status);
    
    /**
     * Start session
     */
    @Modifying
    @Query("UPDATE QuizSession qs SET qs.status = 'ACTIVE', qs.actualStart = :startTime " +
           "WHERE qs.id = :sessionId")
    void startSession(@Param("sessionId") UUID sessionId, @Param("startTime") LocalDateTime startTime);
    
    /**
     * End session
     */
    @Modifying
    @Query("UPDATE QuizSession qs SET qs.status = 'COMPLETED', qs.actualEnd = :endTime " +
           "WHERE qs.id = :sessionId")
    void endSession(@Param("sessionId") UUID sessionId, @Param("endTime") LocalDateTime endTime);
    
    /**
     * Pause session
     */
    @Modifying
    @Query("UPDATE QuizSession qs SET qs.status = 'PAUSED' WHERE qs.id = :sessionId")
    void pauseSession(@Param("sessionId") UUID sessionId);
    
    /**
     * Resume session
     */
    @Modifying
    @Query("UPDATE QuizSession qs SET qs.status = 'ACTIVE' WHERE qs.id = :sessionId")
    void resumeSession(@Param("sessionId") UUID sessionId);
    
    /**
     * Cancel session
     */
    @Modifying
    @Query("UPDATE QuizSession qs SET qs.status = 'CANCELLED' WHERE qs.id = :sessionId")
    void cancelSession(@Param("sessionId") UUID sessionId);
    
    /**
     * Update session settings
     */
    @Modifying
    @Query("UPDATE QuizSession qs SET qs.settings = :settings WHERE qs.id = :sessionId")
    void updateSessionSettings(@Param("sessionId") UUID sessionId, @Param("settings") String settings);
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search sessions by name
     */
    @Query("SELECT qs FROM QuizSession qs WHERE LOWER(qs.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY qs.createdAt DESC")
    Page<QuizSession> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Advanced session search
     */
    @Query("SELECT qs FROM QuizSession qs WHERE " +
           "(:hostId IS NULL OR qs.host.id = :hostId) " +
           "AND (:quizId IS NULL OR qs.quiz.id = :quizId) " +
           "AND (:status IS NULL OR qs.status = :status) " +
           "AND (:sessionCode IS NULL OR qs.sessionCode = :sessionCode) " +
           "AND (:searchTerm IS NULL OR LOWER(qs.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY qs.createdAt DESC")
    Page<QuizSession> searchSessionsWithFilters(@Param("hostId") UUID hostId,
                                              @Param("quizId") UUID quizId,
                                              @Param("status") SessionStatus status,
                                              @Param("sessionCode") String sessionCode,
                                              @Param("searchTerm") String searchTerm,
                                              Pageable pageable);
    
    // ===== ANALYTICS AND REPORTING =====
    
    /**
     * Count sessions by status
     */
    @Query("SELECT qs.status, COUNT(qs) FROM QuizSession qs GROUP BY qs.status")
    List<Object[]> countSessionsByStatus();
    
    /**
     * Get session duration statistics
     */
    @Query("SELECT AVG(EXTRACT(EPOCH FROM (qs.actualEnd - qs.actualStart))/60) as avgDurationMinutes, " +
           "MIN(EXTRACT(EPOCH FROM (qs.actualEnd - qs.actualStart))/60) as minDurationMinutes, " +
           "MAX(EXTRACT(EPOCH FROM (qs.actualEnd - qs.actualStart))/60) as maxDurationMinutes " +
           "FROM QuizSession qs WHERE qs.status = 'COMPLETED' " +
           "AND qs.actualStart IS NOT NULL AND qs.actualEnd IS NOT NULL")
    Object[] getSessionDurationStats();
    
    /**
     * Get daily session statistics
     */
    @Query("SELECT DATE(qs.scheduledStart) as date, COUNT(qs) as sessionCount, " +
           "COUNT(CASE WHEN qs.status = 'COMPLETED' THEN 1 END) as completedCount " +
           "FROM QuizSession qs WHERE qs.scheduledStart BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(qs.scheduledStart) ORDER BY date")
    List<Object[]> getDailySessionStats(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get participation statistics
     */
    @Query("SELECT qs.id, qs.name, " +
           "COUNT(qa) as totalParticipants, " +
           "AVG(qa.percentage) as avgScore, " +
           "COUNT(CASE WHEN qa.status = 'SUBMITTED' THEN 1 END) as completions " +
           "FROM QuizSession qs LEFT JOIN QuizAttempt qa ON qs.id = qa.session.id " +
           "WHERE qs.status = 'COMPLETED' " +
           "GROUP BY qs.id, qs.name ORDER BY totalParticipants DESC")
    List<Object[]> getParticipationStatistics(Pageable pageable);
    
    /**
     * Get most active hosts
     */
    @Query("SELECT u.email, CONCAT(u.firstName, ' ', u.lastName) as hostName, " +
           "COUNT(qs) as sessionCount, " +
           "COUNT(CASE WHEN qs.status = 'COMPLETED' THEN 1 END) as completedSessions " +
           "FROM QuizSession qs JOIN User u ON qs.host.id = u.id " +
           "GROUP BY u.id, u.email, u.firstName, u.lastName ORDER BY sessionCount DESC")
    List<Object[]> getMostActiveHosts(Pageable pageable);
    
    // ===== SESSION CODE MANAGEMENT =====
    
    /**
     * Find sessions with expiring codes
     */
    @Query("SELECT qs FROM QuizSession qs WHERE qs.status = 'SCHEDULED' " +
           "AND qs.scheduledEnd < :threshold")
    List<QuizSession> findSessionsWithExpiringCodes(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Generate unique session code
     */
    @Query("SELECT CASE WHEN COUNT(qs) > 0 THEN true ELSE false END FROM QuizSession qs " +
           "WHERE qs.sessionCode = :code")
    boolean isSessionCodeTaken(@Param("code") String code);
}