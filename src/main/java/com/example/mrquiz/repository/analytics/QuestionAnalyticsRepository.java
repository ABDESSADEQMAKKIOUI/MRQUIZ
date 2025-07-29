package com.example.mrquiz.repository.analytics;

import com.example.mrquiz.entity.analytics.QuestionAnalytics;
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

public interface QuestionAnalyticsRepository extends BaseRepository<QuestionAnalytics> {
    
    // ===== BASIC ANALYTICS QUERIES =====
    
    /**
     * Find analytics by question
     */
    List<QuestionAnalytics> findByQuestionId(UUID questionId);
    
    /**
     * Find analytics by quiz
     */
    List<QuestionAnalytics> findByQuizId(UUID quizId);
    
    /**
     * Find analytics by institution
     */
    List<QuestionAnalytics> findByInstitutionId(UUID institutionId);
    
    /**
     * Find analytics for period
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.question.id = :questionId " +
           "AND qa.periodStart >= :startDate AND qa.periodEnd <= :endDate " +
           "ORDER BY qa.periodStart DESC")
    List<QuestionAnalytics> findByQuestionAndPeriod(@Param("questionId") UUID questionId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Find latest analytics for question
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.question.id = :questionId " +
           "ORDER BY qa.periodEnd DESC LIMIT 1")
    Optional<QuestionAnalytics> findLatestByQuestion(@Param("questionId") UUID questionId);
    
    // ===== PERFORMANCE ANALYSIS =====
    
    /**
     * Find high-performing questions
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.successRate >= :minSuccessRate " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.successRate DESC")
    List<QuestionAnalytics> findHighPerformingQuestions(@Param("minSuccessRate") BigDecimal minSuccessRate,
                                                       @Param("minAttempts") Integer minAttempts,
                                                       Pageable pageable);
    
    /**
     * Find low-performing questions needing review
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.successRate <= :maxSuccessRate " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.successRate ASC")
    List<QuestionAnalytics> findLowPerformingQuestions(@Param("maxSuccessRate") BigDecimal maxSuccessRate,
                                                      @Param("minAttempts") Integer minAttempts,
                                                      Pageable pageable);
    
    /**
     * Find questions with good discrimination
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.discriminationIndex >= :minDiscrimination " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.discriminationIndex DESC")
    List<QuestionAnalytics> findQuestionsWithGoodDiscrimination(@Param("minDiscrimination") BigDecimal minDiscrimination,
                                                               @Param("minAttempts") Integer minAttempts);
    
    /**
     * Find questions with poor discrimination
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.discriminationIndex <= :maxDiscrimination " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.discriminationIndex ASC")
    List<QuestionAnalytics> findQuestionsWithPoorDiscrimination(@Param("maxDiscrimination") BigDecimal maxDiscrimination,
                                                               @Param("minAttempts") Integer minAttempts);
    
    /**
     * Find questions with optimal difficulty
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.difficultyIndex BETWEEN :minDifficulty AND :maxDifficulty " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.difficultyIndex")
    List<QuestionAnalytics> findQuestionsWithOptimalDifficulty(@Param("minDifficulty") BigDecimal minDifficulty,
                                                              @Param("maxDifficulty") BigDecimal maxDifficulty,
                                                              @Param("minAttempts") Integer minAttempts);
    
    // ===== TIME-BASED ANALYSIS =====
    
    /**
     * Find questions taking too long to answer
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.averageTimeSpent > :timeThreshold " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.averageTimeSpent DESC")
    List<QuestionAnalytics> findTimeConsumingQuestions(@Param("timeThreshold") BigDecimal timeThreshold,
                                                      @Param("minAttempts") Integer minAttempts);
    
    /**
     * Find questions answered too quickly (suspicious)
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.averageTimeSpent < :timeThreshold " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.averageTimeSpent ASC")
    List<QuestionAnalytics> findQuicklyAnsweredQuestions(@Param("timeThreshold") BigDecimal timeThreshold,
                                                        @Param("minAttempts") Integer minAttempts);
    
    /**
     * Find questions with efficient time usage
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.timeEfficiencyScore >= :minEfficiency " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.timeEfficiencyScore DESC")
    List<QuestionAnalytics> findTimeEfficientQuestions(@Param("minEfficiency") BigDecimal minEfficiency,
                                                      @Param("minAttempts") Integer minAttempts);
    
    // ===== TEACHER-SPECIFIC ANALYTICS =====
    
    /**
     * Find analytics for teacher's questions
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.question.creator.id = :teacherId " +
           "ORDER BY qa.lastCalculated DESC")
    Page<QuestionAnalytics> findTeacherQuestionAnalytics(@Param("teacherId") UUID teacherId, Pageable pageable);
    
    /**
     * Get teacher's question performance summary
     */
    @Query("SELECT " +
           "COUNT(qa) as totalQuestions, " +
           "AVG(qa.successRate) as avgSuccessRate, " +
           "AVG(qa.difficultyIndex) as avgDifficulty, " +
           "AVG(qa.discriminationIndex) as avgDiscrimination, " +
           "SUM(qa.totalAttempts) as totalAttempts " +
           "FROM QuestionAnalytics qa WHERE qa.question.creator.id = :teacherId")
    Object[] getTeacherQuestionPerformanceSummary(@Param("teacherId") UUID teacherId);
    
    /**
     * Find teacher's best performing questions
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.question.creator.id = :teacherId " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.successRate DESC, qa.discriminationIndex DESC")
    List<QuestionAnalytics> findTeacherBestQuestions(@Param("teacherId") UUID teacherId,
                                                    @Param("minAttempts") Integer minAttempts,
                                                    Pageable pageable);
    
    /**
     * Find teacher's questions needing improvement
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.question.creator.id = :teacherId " +
           "AND (qa.successRate < :minSuccessRate OR qa.discriminationIndex < :minDiscrimination) " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.successRate ASC")
    List<QuestionAnalytics> findTeacherQuestionsNeedingImprovement(@Param("teacherId") UUID teacherId,
                                                                  @Param("minSuccessRate") BigDecimal minSuccessRate,
                                                                  @Param("minDiscrimination") BigDecimal minDiscrimination,
                                                                  @Param("minAttempts") Integer minAttempts);
    
    // ===== INSTITUTIONAL ANALYTICS =====
    
    /**
     * Get institution question performance overview
     */
    @Query("SELECT " +
           "COUNT(qa) as totalQuestions, " +
           "AVG(qa.successRate) as avgSuccessRate, " +
           "AVG(qa.difficultyIndex) as avgDifficulty, " +
           "AVG(qa.discriminationIndex) as avgDiscrimination, " +
           "SUM(qa.totalAttempts) as totalAttempts, " +
           "COUNT(CASE WHEN qa.successRate >= 0.7 THEN 1 END) as goodQuestions, " +
           "COUNT(CASE WHEN qa.successRate < 0.3 THEN 1 END) as poorQuestions " +
           "FROM QuestionAnalytics qa WHERE qa.institution.id = :institutionId")
    Object[] getInstitutionQuestionOverview(@Param("institutionId") UUID institutionId);
    
    /**
     * Find institution's top performing questions
     */
    @Query("SELECT qa FROM QuestionAnalytics qa WHERE qa.institution.id = :institutionId " +
           "AND qa.totalAttempts >= :minAttempts ORDER BY qa.successRate DESC, qa.discriminationIndex DESC")
    List<QuestionAnalytics> findInstitutionTopQuestions(@Param("institutionId") UUID institutionId,
                                                       @Param("minAttempts") Integer minAttempts,
                                                       Pageable pageable);
    
    // ===== QUIZ-SPECIFIC ANALYTICS =====
    
    /**
     * Get quiz question performance distribution
     */
    @Query("SELECT " +
           "COUNT(qa) as totalQuestions, " +
           "AVG(qa.successRate) as avgSuccessRate, " +
           "MIN(qa.successRate) as minSuccessRate, " +
           "MAX(qa.successRate) as maxSuccessRate, " +
           "AVG(qa.averageTimeSpent) as avgTimeSpent " +
           "FROM QuestionAnalytics qa WHERE qa.quiz.id = :quizId")
    Object[] getQuizQuestionDistribution(@Param("quizId") UUID quizId);
    
    /**
     * Find quiz questions by performance tier
     */
    @Query("SELECT qa, " +
           "CASE " +
           "  WHEN qa.successRate >= 0.8 THEN 'EASY' " +
           "  WHEN qa.successRate >= 0.6 THEN 'MEDIUM' " +
           "  WHEN qa.successRate >= 0.4 THEN 'HARD' " +
           "  ELSE 'VERY_HARD' " +
           "END as performanceTier " +
           "FROM QuestionAnalytics qa WHERE qa.quiz.id = :quizId " +
           "ORDER BY qa.successRate DESC")
    List<Object[]> getQuizQuestionsByPerformanceTier(@Param("quizId") UUID quizId);
    
    // ===== TRENDING AND PATTERNS =====
    
    /**
     * Find questions with improving performance
     */
    @Query("SELECT qa1 FROM QuestionAnalytics qa1 WHERE EXISTS (" +
           "  SELECT qa2 FROM QuestionAnalytics qa2 " +
           "  WHERE qa2.question.id = qa1.question.id " +
           "  AND qa2.periodEnd < qa1.periodEnd " +
           "  AND qa2.successRate < qa1.successRate - :improvementThreshold" +
           ") ORDER BY qa1.lastCalculated DESC")
    List<QuestionAnalytics> findImprovingQuestions(@Param("improvementThreshold") BigDecimal improvementThreshold);
    
    /**
     * Find questions with declining performance
     */
    @Query("SELECT qa1 FROM QuestionAnalytics qa1 WHERE EXISTS (" +
           "  SELECT qa2 FROM QuestionAnalytics qa2 " +
           "  WHERE qa2.question.id = qa1.question.id " +
           "  AND qa2.periodEnd < qa1.periodEnd " +
           "  AND qa2.successRate > qa1.successRate + :declineThreshold" +
           ") ORDER BY qa1.lastCalculated DESC")
    List<QuestionAnalytics> findDecliningQuestions(@Param("declineThreshold") BigDecimal declineThreshold);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update analytics data
     */
    @Modifying
    @Query("UPDATE QuestionAnalytics qa SET " +
           "qa.totalAttempts = :totalAttempts, " +
           "qa.correctAttempts = :correctAttempts, " +
           "qa.partiallyCorrectAttempts = :partiallyCorrectAttempts, " +
           "qa.incorrectAttempts = :incorrectAttempts, " +
           "qa.successRate = :successRate, " +
           "qa.averageScore = :averageScore, " +
           "qa.difficultyIndex = :difficultyIndex, " +
           "qa.discriminationIndex = :discriminationIndex, " +
           "qa.lastCalculated = CURRENT_TIMESTAMP " +
           "WHERE qa.id = :analyticsId")
    void updateAnalyticsData(@Param("analyticsId") UUID analyticsId,
                            @Param("totalAttempts") Integer totalAttempts,
                            @Param("correctAttempts") Integer correctAttempts,
                            @Param("partiallyCorrectAttempts") Integer partiallyCorrectAttempts,
                            @Param("incorrectAttempts") Integer incorrectAttempts,
                            @Param("successRate") BigDecimal successRate,
                            @Param("averageScore") BigDecimal averageScore,
                            @Param("difficultyIndex") BigDecimal difficultyIndex,
                            @Param("discriminationIndex") BigDecimal discriminationIndex);
    
    /**
     * Update time analysis data
     */
    @Modifying
    @Query("UPDATE QuestionAnalytics qa SET " +
           "qa.averageTimeSpent = :averageTimeSpent, " +
           "qa.medianTimeSpent = :medianTimeSpent, " +
           "qa.timeEfficiencyScore = :timeEfficiencyScore " +
           "WHERE qa.id = :analyticsId")
    void updateTimeAnalysis(@Param("analyticsId") UUID analyticsId,
                           @Param("averageTimeSpent") BigDecimal averageTimeSpent,
                           @Param("medianTimeSpent") BigDecimal medianTimeSpent,
                           @Param("timeEfficiencyScore") BigDecimal timeEfficiencyScore);
    
    /**
     * Update answer patterns
     */
    @Modifying
    @Query("UPDATE QuestionAnalytics qa SET " +
           "qa.answerDistribution = :answerDistribution, " +
           "qa.commonMistakes = :commonMistakes " +
           "WHERE qa.id = :analyticsId")
    void updateAnswerPatterns(@Param("analyticsId") UUID analyticsId,
                             @Param("answerDistribution") String answerDistribution,
                             @Param("commonMistakes") String commonMistakes);
    
    // ===== REPORTING QUERIES =====
    
    /**
     * Get question performance trends over time
     */
    @Query("SELECT qa.periodStart, qa.periodEnd, qa.successRate, qa.totalAttempts " +
           "FROM QuestionAnalytics qa WHERE qa.question.id = :questionId " +
           "ORDER BY qa.periodStart ASC")
    List<Object[]> getQuestionPerformanceTrends(@Param("questionId") UUID questionId);
    
    /**
     * Get comparative analysis for similar questions
     */
    @Query("SELECT qa.question.id, qa.question.title, qa.successRate, qa.difficultyIndex, qa.discriminationIndex " +
           "FROM QuestionAnalytics qa WHERE qa.question.questionType = :questionType " +
           "AND qa.question.difficultyLevel = :difficultyLevel " +
           "AND qa.totalAttempts >= :minAttempts " +
           "ORDER BY qa.successRate DESC")
    List<Object[]> getComparativeAnalysis(@Param("questionType") String questionType,
                                         @Param("difficultyLevel") String difficultyLevel,
                                         @Param("minAttempts") Integer minAttempts);
    
    /**
     * Get analytics summary by period
     */
    @Query("SELECT qa.periodStart, qa.periodEnd, " +
           "COUNT(qa) as questionCount, " +
           "AVG(qa.successRate) as avgSuccessRate, " +
           "AVG(qa.difficultyIndex) as avgDifficulty, " +
           "SUM(qa.totalAttempts) as totalAttempts " +
           "FROM QuestionAnalytics qa " +
           "WHERE qa.periodStart >= :startDate AND qa.periodEnd <= :endDate " +
           "GROUP BY qa.periodStart, qa.periodEnd " +
           "ORDER BY qa.periodStart ASC")
    List<Object[]> getAnalyticsSummaryByPeriod(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}