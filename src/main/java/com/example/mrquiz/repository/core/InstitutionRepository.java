package com.example.mrquiz.repository.core;

import com.example.mrquiz.entity.core.Institution;
import com.example.mrquiz.enums.InstitutionStatus;
import com.example.mrquiz.enums.InstitutionType;
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

public interface InstitutionRepository extends BaseRepository<Institution> {
    
    // ===== BASIC INSTITUTION QUERIES =====
    
    /**
     * Find institution by slug
     */
    Optional<Institution> findBySlug(String slug);
    
    /**
     * Find institution by domain
     */
    Optional<Institution> findByDomain(String domain);
    
    /**
     * Find institutions by type
     */
    List<Institution> findByType(InstitutionType type);
    
    /**
     * Find institutions by status
     */
    List<Institution> findByStatus(InstitutionStatus status);
    
    /**
     * Find institutions by type and status
     */
    List<Institution> findByTypeAndStatus(InstitutionType type, InstitutionStatus status);
    
    /**
     * Check if slug exists
     */
    boolean existsBySlug(String slug);
    
    /**
     * Check if domain exists
     */
    boolean existsByDomain(String domain);
    
    // ===== MULTI-INSTITUTIONAL SUPPORT =====
    
    /**
     * Find all active institutions
     */
    @Query("SELECT i FROM Institution i WHERE i.status = 'ACTIVE' ORDER BY i.name ASC")
    List<Institution> findActiveInstitutions();
    
    /**
     * Find institutions by subscription tier
     */
    @Query("SELECT i FROM Institution i WHERE i.subscriptionTier = :tier AND i.status = 'ACTIVE'")
    List<Institution> findBySubscriptionTier(@Param("tier") String tier);
    
    /**
     * Find trial institutions
     */
    @Query("SELECT i FROM Institution i WHERE i.status = 'ACTIVE' AND i.trialEndsAt IS NOT NULL " +
           "AND i.trialEndsAt > :now")
    List<Institution> findTrialInstitutions(@Param("now") LocalDateTime now);
    
    /**
     * Find institutions with expiring trials
     */
    @Query("SELECT i FROM Institution i WHERE i.status = 'ACTIVE' AND i.trialEndsAt IS NOT NULL " +
           "AND i.trialEndsAt BETWEEN :now AND :threshold")
    List<Institution> findInstitutionsWithExpiringTrials(@Param("now") LocalDateTime now, 
                                                        @Param("threshold") LocalDateTime threshold);
    
    // ===== SCHOOL DISTRICT MANAGEMENT =====
    
    /**
     * Find school districts (schools with multiple locations)
     */
    @Query("SELECT i FROM Institution i WHERE i.type = 'SCHOOL' " +
           "AND JSON_EXTRACT(i.settings, '$.isDistrict') = true")
    List<Institution> findSchoolDistricts();
    
    /**
     * Find schools in district
     */
    @Query("SELECT i FROM Institution i WHERE i.type = 'SCHOOL' " +
           "AND JSON_EXTRACT(i.settings, '$.districtId') = :districtId")
    List<Institution> findSchoolsInDistrict(@Param("districtId") String districtId);
    
    /**
     * Find institutions with shared resources enabled
     */
    @Query("SELECT i FROM Institution i WHERE JSON_EXTRACT(i.settings, '$.allowResourceSharing') = true " +
           "AND i.status = 'ACTIVE'")
    List<Institution> findInstitutionsWithResourceSharing();
    
    // ===== UNIVERSITY & HIGHER EDUCATION =====
    
    /**
     * Find universities and colleges
     */
    @Query("SELECT i FROM Institution i WHERE i.type IN ('UNIVERSITY', 'COLLEGE') " +
           "AND i.status = 'ACTIVE' ORDER BY i.name ASC")
    List<Institution> findHigherEducationInstitutions();
    
    /**
     * Find institutions with research programs
     */
    @Query("SELECT i FROM Institution i WHERE JSON_EXTRACT(i.features, '$.researchSupport') = true " +
           "AND i.status = 'ACTIVE'")
    List<Institution> findResearchInstitutions();
    
    /**
     * Find institutions with LMS integration
     */
    @Query("SELECT i FROM Institution i WHERE JSON_EXTRACT(i.features, '$.lmsIntegration') = true " +
           "AND i.status = 'ACTIVE'")
    List<Institution> findInstitutionsWithLmsIntegration();
    
    // ===== CORPORATE & TRAINING ORGANIZATIONS =====
    
    /**
     * Find corporate training organizations
     */
    @Query("SELECT i FROM Institution i WHERE i.type = 'CORPORATE' AND i.status = 'ACTIVE' " +
           "ORDER BY i.name ASC")
    List<Institution> findCorporateInstitutions();
    
    /**
     * Find training centers
     */
    @Query("SELECT i FROM Institution i WHERE i.type = 'TRAINING_CENTER' AND i.status = 'ACTIVE' " +
           "ORDER BY i.name ASC")
    List<Institution> findTrainingCenters();
    
    /**
     * Find institutions with certification programs
     */
    @Query("SELECT i FROM Institution i WHERE JSON_EXTRACT(i.features, '$.certificationPrograms') = true " +
           "AND i.status = 'ACTIVE'")
    List<Institution> findCertificationProviders();
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search institutions by name
     */
    @Query("SELECT i FROM Institution i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND i.status = 'ACTIVE'")
    Page<Institution> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Advanced institution search with filters
     */
    @Query("SELECT i FROM Institution i WHERE " +
           "(:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:type IS NULL OR i.type = :type) " +
           "AND (:status IS NULL OR i.status = :status) " +
           "AND (:subscriptionTier IS NULL OR i.subscriptionTier = :subscriptionTier) " +
           "ORDER BY i.name ASC")
    Page<Institution> searchWithFilters(@Param("name") String name,
                                      @Param("type") InstitutionType type,
                                      @Param("status") InstitutionStatus status,
                                      @Param("subscriptionTier") String subscriptionTier,
                                      Pageable pageable);
    
    // ===== FEATURE AND LIMIT MANAGEMENT =====
    
    /**
     * Find institutions with specific feature enabled
     */
    @Query("SELECT i FROM Institution i WHERE JSON_EXTRACT(i.features, CONCAT('$.', :feature)) = true " +
           "AND i.status = 'ACTIVE'")
    List<Institution> findInstitutionsWithFeature(@Param("feature") String feature);
    
    /**
     * Find institutions exceeding usage limits
     */
    @Query("SELECT i FROM Institution i WHERE " +
           "JSON_EXTRACT(i.limits, '$.maxStudents') IS NOT NULL " +
           "AND (SELECT COUNT(ui) FROM UserInstitution ui WHERE ui.institution.id = i.id " +
           "     AND ui.role IN ('STUDENT') AND ui.status = 'ACTIVE') > " +
           "CAST(JSON_EXTRACT(i.limits, '$.maxStudents') AS INTEGER)")
    List<Institution> findInstitutionsExceedingStudentLimit();
    
    /**
     * Find institutions approaching limits
     */
    @Query("SELECT i FROM Institution i WHERE " +
           "JSON_EXTRACT(i.limits, '$.maxStudents') IS NOT NULL " +
           "AND (SELECT COUNT(ui) FROM UserInstitution ui WHERE ui.institution.id = i.id " +
           "     AND ui.role IN ('STUDENT') AND ui.status = 'ACTIVE') > " +
           "(CAST(JSON_EXTRACT(i.limits, '$.maxStudents') AS INTEGER) * 0.8)")
    List<Institution> findInstitutionsApproachingLimits();
    
    // ===== ANALYTICS AND REPORTING =====
    
    /**
     * Get institution user statistics
     */
    @Query("SELECT i.id, i.name, " +
           "COUNT(CASE WHEN ui.role = 'STUDENT' AND ui.status = 'ACTIVE' THEN 1 END) as activeStudents, " +
           "COUNT(CASE WHEN ui.role = 'TEACHER' AND ui.status = 'ACTIVE' THEN 1 END) as activeTeachers, " +
           "COUNT(CASE WHEN ui.role = 'ADMIN' AND ui.status = 'ACTIVE' THEN 1 END) as activeAdmins " +
           "FROM Institution i LEFT JOIN UserInstitution ui ON i.id = ui.institution.id " +
           "WHERE i.status = 'ACTIVE' GROUP BY i.id, i.name ORDER BY activeStudents DESC")
    List<Object[]> getInstitutionUserStatistics();
    
    /**
     * Get institution quiz statistics
     */
    @Query("SELECT i.id, i.name, " +
           "COUNT(DISTINCT q.id) as totalQuizzes, " +
           "COUNT(DISTINCT CASE WHEN q.status = 'PUBLISHED' THEN q.id END) as publishedQuizzes, " +
           "COUNT(DISTINCT qa.id) as totalAttempts " +
           "FROM Institution i " +
           "LEFT JOIN Quiz q ON i.id = q.institution.id " +
           "LEFT JOIN QuizAttempt qa ON q.id = qa.quiz.id " +
           "WHERE i.status = 'ACTIVE' " +
           "GROUP BY i.id, i.name ORDER BY totalQuizzes DESC")
    List<Object[]> getInstitutionQuizStatistics();
    
    /**
     * Get institution growth statistics
     */
    @Query("SELECT i.id, i.name, " +
           "COUNT(CASE WHEN ui.joinedAt > :monthAgo THEN 1 END) as newUsersThisMonth, " +
           "COUNT(CASE WHEN q.createdAt > :monthAgo THEN 1 END) as newQuizzesThisMonth " +
           "FROM Institution i " +
           "LEFT JOIN UserInstitution ui ON i.id = ui.institution.id " +
           "LEFT JOIN Quiz q ON i.id = q.institution.id " +
           "WHERE i.status = 'ACTIVE' " +
           "GROUP BY i.id, i.name")
    List<Object[]> getInstitutionGrowthStats(@Param("monthAgo") LocalDateTime monthAgo);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update institution status
     */
    @Modifying
    @Query("UPDATE Institution i SET i.status = :status WHERE i.id = :institutionId")
    void updateStatus(@Param("institutionId") UUID institutionId, @Param("status") InstitutionStatus status);
    
    /**
     * Update subscription tier
     */
    @Modifying
    @Query("UPDATE Institution i SET i.subscriptionTier = :tier WHERE i.id = :institutionId")
    void updateSubscriptionTier(@Param("institutionId") UUID institutionId, @Param("tier") String tier);
    
    /**
     * Extend trial period
     */
    @Modifying
    @Query("UPDATE Institution i SET i.trialEndsAt = :newEndDate WHERE i.id = :institutionId")
    void extendTrial(@Param("institutionId") UUID institutionId, @Param("newEndDate") LocalDateTime newEndDate);
    
    /**
     * Update institution features
     */
    @Modifying
    @Query("UPDATE Institution i SET i.features = :features WHERE i.id = :institutionId")
    void updateFeatures(@Param("institutionId") UUID institutionId, @Param("features") String features);
    
    /**
     * Update institution limits
     */
    @Modifying
    @Query("UPDATE Institution i SET i.limits = :limits WHERE i.id = :institutionId")
    void updateLimits(@Param("institutionId") UUID institutionId, @Param("limits") String limits);
    
    // ===== COMPLIANCE AND MONITORING =====
    
    /**
     * Find institutions requiring compliance review
     */
    @Query("SELECT i FROM Institution i WHERE JSON_EXTRACT(i.settings, '$.requiresComplianceReview') = true " +
           "OR i.updatedAt < :reviewThreshold")
    List<Institution> findInstitutionsRequiringReview(@Param("reviewThreshold") LocalDateTime reviewThreshold);
    
    /**
     * Find institutions with custom branding
     */
    @Query("SELECT i FROM Institution i WHERE JSON_LENGTH(i.branding) > 0 AND i.status = 'ACTIVE'")
    List<Institution> findInstitutionsWithCustomBranding();
    
    /**
     * Find institutions by geographic region
     */
    @Query("SELECT i FROM Institution i WHERE JSON_EXTRACT(i.address, '$.country') = :country " +
           "AND i.status = 'ACTIVE'")
    List<Institution> findInstitutionsByCountry(@Param("country") String country);
    
    // ===== STATISTICS AND REPORTING =====
    
    /**
     * Count institutions by type
     */
    @Query("SELECT i.type, COUNT(i) FROM Institution i GROUP BY i.type")
    List<Object[]> countInstitutionsByType();
    
    /**
     * Count institutions by status
     */
    @Query("SELECT i.status, COUNT(i) FROM Institution i GROUP BY i.status")
    List<Object[]> countInstitutionsByStatus();
    
    /**
     * Count institutions by subscription tier
     */
    @Query("SELECT i.subscriptionTier, COUNT(i) FROM Institution i WHERE i.status = 'ACTIVE' " +
           "GROUP BY i.subscriptionTier")
    List<Object[]> countInstitutionsBySubscriptionTier();
    
    /**
     * Get institution creation statistics
     */
    @Query("SELECT DATE(i.createdAt) as date, COUNT(i) as count FROM Institution i " +
           "WHERE i.createdAt BETWEEN :start AND :end " +
           "GROUP BY DATE(i.createdAt) ORDER BY date")
    List<Object[]> getInstitutionCreationStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * Get top institutions by user count
     */
    @Query("SELECT i.id, i.name, COUNT(ui) as userCount FROM Institution i " +
           "LEFT JOIN UserInstitution ui ON i.id = ui.institution.id " +
           "WHERE i.status = 'ACTIVE' AND ui.status = 'ACTIVE' " +
           "GROUP BY i.id, i.name ORDER BY userCount DESC")
    List<Object[]> getTopInstitutionsByUserCount(Pageable pageable);
    
    /**
     * Get institution activity summary
     */
    @Query("SELECT i.id, i.name, i.type, i.subscriptionTier, " +
           "COUNT(DISTINCT ui.id) as totalUsers, " +
           "COUNT(DISTINCT q.id) as totalQuizzes, " +
           "COUNT(DISTINCT qa.id) as totalAttempts, " +
           "i.createdAt " +
           "FROM Institution i " +
           "LEFT JOIN UserInstitution ui ON i.id = ui.institution.id " +
           "LEFT JOIN Quiz q ON i.id = q.institution.id " +
           "LEFT JOIN QuizAttempt qa ON q.id = qa.quiz.id " +
           "WHERE i.status = 'ACTIVE' " +
           "GROUP BY i.id, i.name, i.type, i.subscriptionTier, i.createdAt " +
           "ORDER BY totalUsers DESC")
    List<Object[]> getInstitutionActivitySummary();
}