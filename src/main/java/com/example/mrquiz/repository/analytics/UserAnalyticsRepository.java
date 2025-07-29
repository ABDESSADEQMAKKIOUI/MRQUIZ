package com.example.mrquiz.repository.analytics;

import com.example.mrquiz.entity.analytics.UserAnalytics;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAnalyticsRepository extends BaseRepository<UserAnalytics> {
    
    // ===== BASIC ANALYTICS QUERIES =====
    
    /**
     * Find analytics by user
     */
    List<UserAnalytics> findByUserId(UUID userId);
    
    /**
     * Find analytics by course
     */
    List<UserAnalytics> findByCourseId(UUID courseId);
    
    /**
     * Find analytics by institution
     */
    List<UserAnalytics> findByInstitutionId(UUID institutionId);
    
    /**
     * Find analytics for user and course
     */
    Optional<UserAnalytics> findByUserIdAndCourseId(UUID userId, UUID courseId);
    
    /**
     * Find analytics for period
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.user.id = :userId " +
           "AND ua.periodStart >= :startDate AND ua.periodEnd <= :endDate " +
           "ORDER BY ua.periodStart DESC")
    List<UserAnalytics> findByUserAndPeriod(@Param("userId") UUID userId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * Find latest analytics for user
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.user.id = :userId " +
           "ORDER BY ua.lastUpdated DESC LIMIT 1")
    Optional<UserAnalytics> findLatestByUser(@Param("userId") UUID userId);
    
    // ===== STUDENT PERFORMANCE ANALYSIS =====
    
    /**
     * Find high-performing students
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.averageScore >= :minScore " +
           "AND ua.totalQuizzesCompleted >= :minQuizzes " +
           "ORDER BY ua.averageScore DESC, ua.consistencyScore DESC")
    List<UserAnalytics> findHighPerformingStudents(@Param("minScore") BigDecimal minScore,
                                                  @Param("minQuizzes") Integer minQuizzes,
                                                  Pageable pageable);
    
    /**
     * Find students needing support
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.averageScore <= :maxScore " +
           "AND ua.totalQuizzesCompleted >= :minQuizzes " +
           "ORDER BY ua.averageScore ASC, ua.improvementTrend ASC")
    List<UserAnalytics> findStudentsNeedingSupport(@Param("maxScore") BigDecimal maxScore,
                                                  @Param("minQuizzes") Integer minQuizzes,
                                                  Pageable pageable);
    
    /**
     * Find students with improving performance
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.improvementTrend >= :minImprovement " +
           "AND ua.totalQuizzesCompleted >= :minQuizzes " +
           "ORDER BY ua.improvementTrend DESC")
    List<UserAnalytics> findImprovingStudents(@Param("minImprovement") BigDecimal minImprovement,
                                            @Param("minQuizzes") Integer minQuizzes);
    
    /**
     * Find students with declining performance
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.improvementTrend <= :maxDecline " +
           "AND ua.totalQuizzesCompleted >= :minQuizzes " +
           "ORDER BY ua.improvementTrend ASC")
    List<UserAnalytics> findDecliningStudents(@Param("maxDecline") BigDecimal maxDecline,
                                            @Param("minQuizzes") Integer minQuizzes);
    
    /**
     * Find consistent performers
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.consistencyScore >= :minConsistency " +
           "AND ua.totalQuizzesCompleted >= :minQuizzes " +
           "ORDER BY ua.consistencyScore DESC")
    List<UserAnalytics> findConsistentPerformers(@Param("minConsistency") BigDecimal minConsistency,
                                                @Param("minQuizzes") Integer minQuizzes);
    
    // ===== COURSE-SPECIFIC ANALYTICS =====
    
    /**
     * Find top students in course
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.course.id = :courseId " +
           "AND ua.totalQuizzesCompleted >= :minQuizzes " +
           "ORDER BY ua.averageScore DESC, ua.totalQuizzesCompleted DESC")
    List<UserAnalytics> findTopStudentsInCourse(@Param("courseId") UUID courseId,
                                               @Param("minQuizzes") Integer minQuizzes,
                                               Pageable pageable);
    
    /**
     * Get course performance distribution
     */
    @Query("SELECT " +
           "COUNT(ua) as totalStudents, " +
           "AVG(ua.averageScore) as classAverage, " +
           "MIN(ua.averageScore) as minScore, " +
           "MAX(ua.averageScore) as maxScore, " +
           "AVG(ua.totalQuizzesCompleted) as avgQuizzesCompleted, " +
           "AVG(ua.participationRate) as avgParticipation " +
           "FROM UserAnalytics ua WHERE ua.course.id = :courseId")
    Object[] getCoursePerformanceDistribution(@Param("courseId") UUID courseId);
    
    /**
     * Find students by performance tier in course
     */
    @Query("SELECT ua, " +
           "CASE " +
           "  WHEN ua.averageScore >= 90 THEN 'EXCELLENT' " +
           "  WHEN ua.averageScore >= 80 THEN 'GOOD' " +
           "  WHEN ua.averageScore >= 70 THEN 'SATISFACTORY' " +
           "  WHEN ua.averageScore >= 60 THEN 'NEEDS_IMPROVEMENT' " +
           "  ELSE 'POOR' " +
           "END as performanceTier " +
           "FROM UserAnalytics ua WHERE ua.course.id = :courseId " +
           "ORDER BY ua.averageScore DESC")
    List<Object[]> getStudentsByPerformanceTier(@Param("courseId") UUID courseId);
    
    // ===== INSTITUTIONAL ANALYTICS =====
    
    /**
     * Get institution student performance overview
     */
    @Query("SELECT " +
           "COUNT(ua) as totalStudents, " +
           "AVG(ua.averageScore) as institutionAverage, " +
           "AVG(ua.totalQuizzesCompleted) as avgQuizzesCompleted, " +
           "AVG(ua.participationRate) as avgParticipation, " +
           "COUNT(CASE WHEN ua.averageScore >= 80 THEN 1 END) as highPerformers, " +
           "COUNT(CASE WHEN ua.averageScore < 60 THEN 1 END) as strugglingStudents " +
           "FROM UserAnalytics ua WHERE ua.institution.id = :institutionId")
    Object[] getInstitutionPerformanceOverview(@Param("institutionId") UUID institutionId);
    
    /**
     * Find institution's top performers
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.institution.id = :institutionId " +
           "AND ua.totalQuizzesCompleted >= :minQuizzes " +
           "ORDER BY ua.averageScore DESC, ua.consistencyScore DESC")
    List<UserAnalytics> findInstitutionTopPerformers(@Param("institutionId") UUID institutionId,
                                                    @Param("minQuizzes") Integer minQuizzes,
                                                    Pageable pageable);
    
    // ===== ENGAGEMENT AND PARTICIPATION =====
    
    /**
     * Find highly engaged students
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.participationRate >= :minParticipation " +
           "AND ua.totalQuizzesTaken >= :minQuizzes " +
           "ORDER BY ua.participationRate DESC, ua.totalQuizzesTaken DESC")
    List<UserAnalytics> findHighlyEngagedStudents(@Param("minParticipation") BigDecimal minParticipation,
                                                 @Param("minQuizzes") Integer minQuizzes);
    
    /**
     * Find students with low engagement
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.participationRate <= :maxParticipation " +
           "ORDER BY ua.participationRate ASC, ua.totalQuizzesTaken ASC")
    List<UserAnalytics> findLowEngagementStudents(@Param("maxParticipation") BigDecimal maxParticipation);
    
    /**
     * Find students who review frequently
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.reviewFrequency >= :minReviews " +
           "ORDER BY ua.reviewFrequency DESC")
    List<UserAnalytics> findFrequentReviewers(@Param("minReviews") Integer minReviews);
    
    // ===== LEARNING PATTERNS AND INSIGHTS =====
    
    /**
     * Find students with specific learning patterns
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE " +
           "JSON_EXTRACT(ua.preferredQuestionTypes, CONCAT('$.', :questionType)) > :threshold")
    List<UserAnalytics> findStudentsByPreferredQuestionType(@Param("questionType") String questionType,
                                                           @Param("threshold") Double threshold);
    
    /**
     * Find students strong in specific topics
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE :topic MEMBER OF ua.strongestTopics")
    List<UserAnalytics> findStudentsStrongInTopic(@Param("topic") String topic);
    
    /**
     * Find students weak in specific topics
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE :topic MEMBER OF ua.weakestTopics")
    List<UserAnalytics> findStudentsWeakInTopic(@Param("topic") String topic);
    
    /**
     * Find students with high learning velocity
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.learningVelocity >= :minVelocity " +
           "ORDER BY ua.learningVelocity DESC")
    List<UserAnalytics> findFastLearners(@Param("minVelocity") BigDecimal minVelocity);
    
    /**
     * Find students needing more time
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.learningVelocity <= :maxVelocity " +
           "AND ua.totalQuizzesCompleted >= :minQuizzes " +
           "ORDER BY ua.learningVelocity ASC")
    List<UserAnalytics> findSlowLearners(@Param("maxVelocity") BigDecimal maxVelocity,
                                       @Param("minQuizzes") Integer minQuizzes);
    
    // ===== TIME-BASED ANALYSIS =====
    
    /**
     * Find students with efficient time usage
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.timeEfficiencyScore >= :minEfficiency " +
           "ORDER BY ua.timeEfficiencyScore DESC")
    List<UserAnalytics> findTimeEfficientStudents(@Param("minEfficiency") BigDecimal minEfficiency);
    
    /**
     * Find students spending too much time
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.averageTimePerQuiz > :timeThreshold " +
           "ORDER BY ua.averageTimePerQuiz DESC")
    List<UserAnalytics> findTimeConsumingStudents(@Param("timeThreshold") Integer timeThreshold);
    
    // ===== TEACHER-SPECIFIC QUERIES =====
    
    /**
     * Get analytics for teacher's students
     */
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.course.instructor.id = :teacherId " +
           "ORDER BY ua.lastUpdated DESC")
    Page<UserAnalytics> findTeacherStudentAnalytics(@Param("teacherId") UUID teacherId, Pageable pageable);
    
    /**
     * Get teacher's class performance summary
     */
    @Query("SELECT " +
           "COUNT(ua) as totalStudents, " +
           "AVG(ua.averageScore) as classAverage, " +
           "AVG(ua.participationRate) as avgParticipation, " +
           "COUNT(CASE WHEN ua.averageScore >= 80 THEN 1 END) as highPerformers, " +
           "COUNT(CASE WHEN ua.averageScore < 60 THEN 1 END) as strugglingStudents, " +
           "AVG(ua.improvementTrend) as avgImprovement " +
           "FROM UserAnalytics ua WHERE ua.course.instructor.id = :teacherId")
    Object[] getTeacherClassSummary(@Param("teacherId") UUID teacherId);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update user quiz statistics
     */
    @Modifying
    @Query("UPDATE UserAnalytics ua SET " +
           "ua.totalQuizzesTaken = :quizzesTaken, " +
           "ua.totalQuizzesCompleted = :quizzesCompleted, " +
           "ua.totalQuestionsAnswered = :questionsAnswered, " +
           "ua.correctAnswers = :correctAnswers, " +
           "ua.partiallyCorrectAnswers = :partiallyCorrectAnswers, " +
           "ua.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE ua.id = :analyticsId")
    void updateQuizStatistics(@Param("analyticsId") UUID analyticsId,
                             @Param("quizzesTaken") Integer quizzesTaken,
                             @Param("quizzesCompleted") Integer quizzesCompleted,
                             @Param("questionsAnswered") Integer questionsAnswered,
                             @Param("correctAnswers") Integer correctAnswers,
                             @Param("partiallyCorrectAnswers") Integer partiallyCorrectAnswers);
    
    /**
     * Update performance metrics
     */
    @Modifying
    @Query("UPDATE UserAnalytics ua SET " +
           "ua.averageScore = :averageScore, " +
           "ua.medianScore = :medianScore, " +
           "ua.bestScore = :bestScore, " +
           "ua.worstScore = :worstScore, " +
           "ua.improvementTrend = :improvementTrend, " +
           "ua.consistencyScore = :consistencyScore " +
           "WHERE ua.id = :analyticsId")
    void updatePerformanceMetrics(@Param("analyticsId") UUID analyticsId,
                                 @Param("averageScore") BigDecimal averageScore,
                                 @Param("medianScore") BigDecimal medianScore,
                                 @Param("bestScore") BigDecimal bestScore,
                                 @Param("worstScore") BigDecimal worstScore,
                                 @Param("improvementTrend") BigDecimal improvementTrend,
                                 @Param("consistencyScore") BigDecimal consistencyScore);
    
    /**
     * Update time analysis
     */
    @Modifying
    @Query("UPDATE UserAnalytics ua SET " +
           "ua.totalTimeSpent = :totalTimeSpent, " +
           "ua.averageTimePerQuiz = :averageTimePerQuiz, " +
           "ua.timeEfficiencyScore = :timeEfficiencyScore " +
           "WHERE ua.id = :analyticsId")
    void updateTimeAnalysis(@Param("analyticsId") UUID analyticsId,
                           @Param("totalTimeSpent") Integer totalTimeSpent,
                           @Param("averageTimePerQuiz") Integer averageTimePerQuiz,
                           @Param("timeEfficiencyScore") BigDecimal timeEfficiencyScore);
    
    /**
     * Update learning patterns
     */
    @Modifying
    @Query("UPDATE UserAnalytics ua SET " +
           "ua.preferredQuestionTypes = :preferredQuestionTypes, " +
           "ua.strongestTopics = :strongestTopics, " +
           "ua.weakestTopics = :weakestTopics, " +
           "ua.learningVelocity = :learningVelocity " +
           "WHERE ua.id = :analyticsId")
    void updateLearningPatterns(@Param("analyticsId") UUID analyticsId,
                               @Param("preferredQuestionTypes") String preferredQuestionTypes,
                               @Param("strongestTopics") List<String> strongestTopics,
                               @Param("weakestTopics") List<String> weakestTopics,
                               @Param("learningVelocity") BigDecimal learningVelocity);
    
    /**
     * Update engagement metrics
     */
    @Modifying
    @Query("UPDATE UserAnalytics ua SET " +
           "ua.participationRate = :participationRate, " +
           "ua.completionRate = :completionRate, " +
           "ua.reviewFrequency = :reviewFrequency " +
           "WHERE ua.id = :analyticsId")
    void updateEngagementMetrics(@Param("analyticsId") UUID analyticsId,
                                @Param("participationRate") BigDecimal participationRate,
                                @Param("completionRate") BigDecimal completionRate,
                                @Param("reviewFrequency") Integer reviewFrequency);
    
    // ===== REPORTING QUERIES =====
    
    /**
     * Get student progress over time
     */
    @Query("SELECT ua.periodStart, ua.periodEnd, ua.averageScore, ua.participationRate " +
           "FROM UserAnalytics ua WHERE ua.user.id = :userId " +
           "ORDER BY ua.periodStart ASC")
    List<Object[]> getStudentProgressOverTime(@Param("userId") UUID userId);
    
    /**
     * Get course performance trends
     */
    @Query("SELECT ua.periodStart, ua.periodEnd, " +
           "AVG(ua.averageScore) as avgScore, " +
           "AVG(ua.participationRate) as avgParticipation, " +
           "COUNT(ua) as studentCount " +
           "FROM UserAnalytics ua WHERE ua.course.id = :courseId " +
           "GROUP BY ua.periodStart, ua.periodEnd " +
           "ORDER BY ua.periodStart ASC")
    List<Object[]> getCoursePerformanceTrends(@Param("courseId") UUID courseId);
    
    /**
     * Get learning pattern analysis
     */
    @Query("SELECT " +
           "ua.preferredQuestionTypes, " +
           "COUNT(ua) as studentCount, " +
           "AVG(ua.averageScore) as avgScore " +
           "FROM UserAnalytics ua " +
           "WHERE ua.institution.id = :institutionId " +
           "GROUP BY ua.preferredQuestionTypes")
    List<Object[]> getLearningPatternAnalysis(@Param("institutionId") UUID institutionId);
    
    /**
     * Get subject area performance
     */
    @Query("SELECT topic, COUNT(ua) as studentCount, AVG(ua.averageScore) as avgScore " +
           "FROM UserAnalytics ua JOIN ua.strongestTopics topic " +
           "WHERE ua.institution.id = :institutionId " +
           "GROUP BY topic ORDER BY avgScore DESC")
    List<Object[]> getSubjectAreaPerformance(@Param("institutionId") UUID institutionId);
}