package com.example.mrquiz.repository.quiz;

import com.example.mrquiz.entity.quiz.QuizAttempt;
import com.example.mrquiz.enums.AttemptStatus;
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

public interface QuizAttemptRepository extends BaseRepository<QuizAttempt> {
    
    // ===== BASIC ATTEMPT QUERIES =====
    
    /**
     * Find attempts by user
     */
    Page<QuizAttempt> findByUserId(UUID userId, Pageable pageable);
    
    /**
     * Find attempts by quiz
     */
    List<QuizAttempt> findByQuizId(UUID quizId);
    
    /**
     * Find attempts by user and quiz
     */
    List<QuizAttempt> findByUserIdAndQuizIdOrderByAttemptNumberDesc(UUID userId, UUID quizId);
    
    /**
     * Find attempt by user, quiz, and attempt number
     */
    Optional<QuizAttempt> findByUserIdAndQuizIdAndAttemptNumber(UUID userId, UUID quizId, Integer attemptNumber);
    
    /**
     * Find latest attempt by user and quiz
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId " +
           "ORDER BY qa.attemptNumber DESC LIMIT 1")
    Optional<QuizAttempt> findLatestAttempt(@Param("userId") UUID userId, @Param("quizId") UUID quizId);
    
    // ===== STUDENT ACCESS AND INVITATION SUPPORT =====
    
    /**
     * Find student's quiz attempts with status
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :studentId AND qa.status = :status " +
           "ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findStudentAttemptsByStatus(@Param("studentId") UUID studentId, 
                                                 @Param("status") AttemptStatus status);
    
    /**
     * Find student's completed attempts
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :studentId " +
           "AND qa.status IN ('SUBMITTED', 'GRADED') ORDER BY qa.submittedAt DESC")
    List<QuizAttempt> findStudentCompletedAttempts(@Param("studentId") UUID studentId);
    
    /**
     * Find student's in-progress attempts
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :studentId AND qa.status = 'IN_PROGRESS' " +
           "ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findStudentInProgressAttempts(@Param("studentId") UUID studentId);
    
    /**
     * Check if student can attempt quiz (based on attempt limits)
     */
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId")
    Long countUserAttempts(@Param("userId") UUID userId, @Param("quizId") UUID quizId);
    
    /**
     * Find attempts by guest users (no registration required)
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE JSON_EXTRACT(qa.metadata, '$.guestSession') = true")
    List<QuizAttempt> findGuestAttempts();
    
    // ===== TEACHER QUIZ MONITORING =====
    
    /**
     * Find all attempts for teacher's quizzes
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.creator.id = :teacherId " +
           "ORDER BY qa.startedAt DESC")
    Page<QuizAttempt> findTeacherQuizAttempts(@Param("teacherId") UUID teacherId, Pageable pageable);
    
    /**
     * Find attempts for specific teacher's quiz
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId AND qa.quiz.creator.id = :teacherId " +
           "ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findTeacherQuizAttempts(@Param("teacherId") UUID teacherId, @Param("quizId") UUID quizId);
    
    /**
     * Find recent attempts for teacher's quizzes
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.creator.id = :teacherId " +
           "AND qa.startedAt > :since ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findRecentTeacherQuizAttempts(@Param("teacherId") UUID teacherId, 
                                                   @Param("since") LocalDateTime since);
    
    /**
     * Count attempts by status for teacher's quizzes
     */
    @Query("SELECT qa.status, COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz.creator.id = :teacherId " +
           "GROUP BY qa.status")
    List<Object[]> countTeacherQuizAttemptsByStatus(@Param("teacherId") UUID teacherId);
    
    // ===== LIVE QUIZ SESSIONS =====
    
    /**
     * Find attempts in live session
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.session.id = :sessionId ORDER BY qa.startedAt ASC")
    List<QuizAttempt> findSessionAttempts(@Param("sessionId") UUID sessionId);
    
    /**
     * Find active participants in live session
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.session.id = :sessionId " +
           "AND qa.status = 'IN_PROGRESS' ORDER BY qa.startedAt ASC")
    List<QuizAttempt> findActiveSessionParticipants(@Param("sessionId") UUID sessionId);
    
    /**
     * Get live session leaderboard
     */
    @Query("SELECT qa.user.id, CONCAT(qa.user.firstName, ' ', qa.user.lastName) as name, " +
           "qa.score, qa.percentage, qa.timeSpent FROM QuizAttempt qa " +
           "WHERE qa.session.id = :sessionId AND qa.status IN ('SUBMITTED', 'GRADED') " +
           "ORDER BY qa.percentage DESC, qa.timeSpent ASC")
    List<Object[]> getSessionLeaderboard(@Param("sessionId") UUID sessionId);
    
    // ===== ANALYTICS AND PERFORMANCE TRACKING =====
    
    /**
     * Find attempts with performance data
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.percentage IS NOT NULL " +
           "AND qa.status = 'GRADED' ORDER BY qa.percentage DESC")
    List<QuizAttempt> findGradedAttempts(Pageable pageable);
    
    /**
     * Get quiz performance statistics
     */
    @Query("SELECT " +
           "COUNT(qa) as totalAttempts, " +
           "COUNT(DISTINCT qa.user.id) as uniqueStudents, " +
           "AVG(qa.percentage) as averageScore, " +
           "MIN(qa.percentage) as minScore, " +
           "MAX(qa.percentage) as maxScore, " +
           "COUNT(CASE WHEN qa.passed = true THEN 1 END) as passedAttempts, " +
           "AVG(qa.timeSpent) as averageTimeSpent " +
           "FROM QuizAttempt qa WHERE qa.quiz.id = :quizId AND qa.status = 'GRADED'")
    Object[] getQuizPerformanceStats(@Param("quizId") UUID quizId);
    
    /**
     * Get student performance across all quizzes
     */
    @Query("SELECT " +
           "COUNT(qa) as totalAttempts, " +
           "AVG(qa.percentage) as averageScore, " +
           "COUNT(CASE WHEN qa.passed = true THEN 1 END) as passedAttempts, " +
           "COUNT(CASE WHEN qa.status = 'GRADED' THEN 1 END) as completedAttempts " +
           "FROM QuizAttempt qa WHERE qa.user.id = :studentId")
    Object[] getStudentPerformanceStats(@Param("studentId") UUID studentId);
    
    /**
     * Find top performing students
     */
    @Query("SELECT qa.user.id, CONCAT(qa.user.firstName, ' ', qa.user.lastName) as name, " +
           "AVG(qa.percentage) as averageScore, COUNT(qa) as attemptCount " +
           "FROM QuizAttempt qa WHERE qa.status = 'GRADED' " +
           "GROUP BY qa.user.id, qa.user.firstName, qa.user.lastName " +
           "HAVING COUNT(qa) >= :minAttempts ORDER BY averageScore DESC")
    List<Object[]> findTopPerformingStudents(@Param("minAttempts") Long minAttempts, Pageable pageable);
    
    // ===== TIME AND PROGRESS TRACKING =====
    
    /**
     * Find attempts that exceeded time limit
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.timeLimit IS NOT NULL " +
           "AND qa.timeSpent > (qa.timeLimit * 60)")
    List<QuizAttempt> findOvertimeAttempts();
    
    /**
     * Find abandoned attempts (started but not submitted)
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.status = 'IN_PROGRESS' " +
           "AND qa.startedAt < :threshold")
    List<QuizAttempt> findAbandonedAttempts(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Find attempts with suspicious timing patterns
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.timeSpent < :minTime " +
           "AND qa.status = 'SUBMITTED' AND qa.quiz.timeLimit IS NOT NULL")
    List<QuizAttempt> findSuspiciouslyFastAttempts(@Param("minTime") Integer minTime);
    
    // ===== PROCTORING AND SECURITY =====
    
    /**
     * Find flagged attempts
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.status = 'FLAGGED' ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findFlaggedAttempts();
    
    /**
     * Find attempts with security incidents
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE JSON_LENGTH(qa.securityFlags) > 0")
    List<QuizAttempt> findAttemptsWithSecurityIssues();
    
    /**
     * Find attempts requiring manual review
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE JSON_EXTRACT(qa.metadata, '$.requiresReview') = true")
    List<QuizAttempt> findAttemptsRequiringReview();
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update attempt status
     */
    @Modifying
    @Query("UPDATE QuizAttempt qa SET qa.status = :status WHERE qa.id = :attemptId")
    void updateAttemptStatus(@Param("attemptId") UUID attemptId, @Param("status") AttemptStatus status);
    
    /**
     * Update attempt score and grade
     */
    @Modifying
    @Query("UPDATE QuizAttempt qa SET qa.score = :score, qa.percentage = :percentage, " +
           "qa.grade = :grade, qa.passed = :passed, qa.gradedAt = :gradedAt " +
           "WHERE qa.id = :attemptId")
    void updateAttemptGrade(@Param("attemptId") UUID attemptId,
                           @Param("score") BigDecimal score,
                           @Param("percentage") BigDecimal percentage,
                           @Param("grade") String grade,
                           @Param("passed") Boolean passed,
                           @Param("gradedAt") LocalDateTime gradedAt);
    
    /**
     * Submit attempt
     */
    @Modifying
    @Query("UPDATE QuizAttempt qa SET qa.status = 'SUBMITTED', qa.submittedAt = :submittedAt, " +
           "qa.timeSpent = :timeSpent WHERE qa.id = :attemptId")
    void submitAttempt(@Param("attemptId") UUID attemptId,
                      @Param("submittedAt") LocalDateTime submittedAt,
                      @Param("timeSpent") Integer timeSpent);
    
    // ===== EMAIL INVITATION TRACKING =====
    
    /**
     * Find attempts from email invitations
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE JSON_EXTRACT(qa.metadata, '$.invitationSource') = 'email'")
    List<QuizAttempt> findEmailInvitationAttempts();
    
    /**
     * Track email invitation conversion rates
     */
    @Query("SELECT " +
           "JSON_EXTRACT(qa.metadata, '$.invitationId') as invitationId, " +
           "COUNT(qa) as attempts, " +
           "COUNT(CASE WHEN qa.status IN ('SUBMITTED', 'GRADED') THEN 1 END) as completions " +
           "FROM QuizAttempt qa WHERE JSON_EXTRACT(qa.metadata, '$.invitationSource') = 'email' " +
           "GROUP BY JSON_EXTRACT(qa.metadata, '$.invitationId')")
    List<Object[]> getEmailInvitationConversionStats();
    
    // ===== QR CODE ACCESS TRACKING =====
    
    /**
     * Find attempts from QR code access
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE JSON_EXTRACT(qa.metadata, '$.accessMethod') = 'qr_code'")
    List<QuizAttempt> findQrCodeAttempts();
    
    /**
     * Track QR code usage statistics
     */
    @Query("SELECT " +
           "JSON_EXTRACT(qa.metadata, '$.qrCodeId') as qrCodeId, " +
           "COUNT(qa) as scans, " +
           "COUNT(DISTINCT qa.user.id) as uniqueUsers " +
           "FROM QuizAttempt qa WHERE JSON_EXTRACT(qa.metadata, '$.accessMethod') = 'qr_code' " +
           "GROUP BY JSON_EXTRACT(qa.metadata, '$.qrCodeId')")
    List<Object[]> getQrCodeUsageStats();
    
    // ===== REPORTING AND STATISTICS =====
    
    /**
     * Get attempt statistics by date
     */
    @Query("SELECT DATE(qa.startedAt) as date, COUNT(qa) as attempts, " +
           "COUNT(CASE WHEN qa.status IN ('SUBMITTED', 'GRADED') THEN 1 END) as completions " +
           "FROM QuizAttempt qa WHERE qa.startedAt BETWEEN :start AND :end " +
           "GROUP BY DATE(qa.startedAt) ORDER BY date")
    List<Object[]> getAttemptStatsByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * Get teacher's quiz engagement metrics
     */
    @Query("SELECT q.id, q.title, " +
           "COUNT(DISTINCT qa.user.id) as uniqueParticipants, " +
           "COUNT(qa) as totalAttempts, " +
           "AVG(qa.percentage) as averageScore, " +
           "COUNT(CASE WHEN qa.status IN ('SUBMITTED', 'GRADED') THEN 1 END) as completions " +
           "FROM Quiz q LEFT JOIN QuizAttempt qa ON q.id = qa.quiz.id " +
           "WHERE q.creator.id = :teacherId " +
           "GROUP BY q.id, q.title ORDER BY uniqueParticipants DESC")
    List<Object[]> getTeacherQuizEngagementMetrics(@Param("teacherId") UUID teacherId);
}