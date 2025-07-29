package com.example.mrquiz.repository.auth;

import com.example.mrquiz.entity.auth.UserProfile;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends BaseRepository<UserProfile> {
    
    // ===== BASIC PROFILE QUERIES =====
    
    /**
     * Find profile by user ID
     */
    Optional<UserProfile> findByUserId(UUID userId);
    
    /**
     * Check if profile exists for user
     */
    boolean existsByUserId(UUID userId);
    
    // ===== TEACHER PORTFOLIO QUERIES =====
    
    /**
     * Find teachers with professional certifications
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_LENGTH(up.certifications) > 0")
    List<UserProfile> findTeachersWithCertifications();
    
    /**
     * Find teachers by academic qualification
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_EXTRACT(up.academicInfo, '$.highestDegree') = :degree")
    List<UserProfile> findTeachersByDegree(@Param("degree") String degree);
    
    /**
     * Find teachers by years of experience
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND CAST(JSON_EXTRACT(up.professionalInfo, '$.yearsOfExperience') AS INTEGER) >= :minYears")
    List<UserProfile> findTeachersByExperience(@Param("minYears") Integer minYears);
    
    /**
     * Find teachers with social media presence
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND (up.linkedinUrl IS NOT NULL OR up.twitterHandle IS NOT NULL OR up.website IS NOT NULL)")
    List<UserProfile> findTeachersWithSocialPresence();
    
    // ===== PERFORMANCE AND STATISTICS =====
    
    /**
     * Find top performing teachers by average quiz scores
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND up.averageScore IS NOT NULL " +
           "ORDER BY up.averageScore DESC")
    List<UserProfile> findTopPerformingTeachers();
    
    /**
     * Find most productive teachers by quiz creation
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "ORDER BY up.totalQuizzesCreated DESC")
    List<UserProfile> findMostProductiveTeachers();
    
    /**
     * Find teachers with minimum quiz creation threshold
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND up.totalQuizzesCreated >= :minQuizzes")
    List<UserProfile> findActiveContentCreators(@Param("minQuizzes") Integer minQuizzes);
    
    // ===== NOTIFICATION AND PRIVACY SETTINGS =====
    
    /**
     * Find users with specific notification preferences
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE JSON_EXTRACT(up.notificationPreferences, '$.emailNotifications') = :enabled")
    List<UserProfile> findByEmailNotificationPreference(@Param("enabled") Boolean enabled);
    
    /**
     * Find users with SMS notifications enabled
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE JSON_EXTRACT(up.notificationPreferences, '$.smsNotifications') = true")
    List<UserProfile> findUsersWithSmsEnabled();
    
    /**
     * Find users with public profiles
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE JSON_EXTRACT(up.privacySettings, '$.publicProfile') = true")
    List<UserProfile> findPublicProfiles();
    
    // ===== ACCESSIBILITY FEATURES =====
    
    /**
     * Find users with accessibility needs
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE JSON_LENGTH(up.accessibilitySettings) > 0")
    List<UserProfile> findUsersWithAccessibilityNeeds();
    
    /**
     * Find users with screen reader requirements
     */
    @Query("SELECT up FROM UserProfile up " +
           "WHERE JSON_EXTRACT(up.accessibilitySettings, '$.screenReader') = true")
    List<UserProfile> findScreenReaderUsers();
    
    // ===== BULK UPDATE OPERATIONS =====
    
    /**
     * Update quiz statistics for user
     */
    @Modifying
    @Query("UPDATE UserProfile up SET " +
           "up.totalQuizzesTaken = :quizzesTaken, " +
           "up.totalQuizzesCreated = :quizzesCreated, " +
           "up.averageScore = :averageScore " +
           "WHERE up.user.id = :userId")
    void updateQuizStatistics(@Param("userId") UUID userId,
                             @Param("quizzesTaken") Integer quizzesTaken,
                             @Param("quizzesCreated") Integer quizzesCreated,
                             @Param("averageScore") BigDecimal averageScore);
    
    /**
     * Update notification preferences
     */
    @Modifying
    @Query("UPDATE UserProfile up SET up.notificationPreferences = :preferences WHERE up.user.id = :userId")
    void updateNotificationPreferences(@Param("userId") UUID userId, @Param("preferences") String preferences);
    
    /**
     * Update privacy settings
     */
    @Modifying
    @Query("UPDATE UserProfile up SET up.privacySettings = :settings WHERE up.user.id = :userId")
    void updatePrivacySettings(@Param("userId") UUID userId, @Param("settings") String settings);
    
    /**
     * Update professional information
     */
    @Modifying
    @Query("UPDATE UserProfile up SET up.professionalInfo = :info WHERE up.user.id = :userId")
    void updateProfessionalInfo(@Param("userId") UUID userId, @Param("info") String info);
    
    // ===== SEARCH AND DISCOVERY =====
    
    /**
     * Search teacher profiles by bio content
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND LOWER(up.bio) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserProfile> searchTeachersByBio(@Param("searchTerm") String searchTerm);
    
    /**
     * Find teachers by specialization in professional info
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_EXTRACT(up.professionalInfo, '$.specialization') LIKE %:specialization%")
    List<UserProfile> findBySpecialization(@Param("specialization") String specialization);
    
    /**
     * Find teachers by institution in academic info
     */
    @Query("SELECT up FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_EXTRACT(up.academicInfo, '$.institution') LIKE %:institution%")
    List<UserProfile> findByAcademicInstitution(@Param("institution") String institution);
    
    // ===== ANALYTICS SUPPORT =====
    
    /**
     * Get profile completion statistics
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN up.bio IS NOT NULL THEN 1 END) as bioCount, " +
           "COUNT(CASE WHEN up.website IS NOT NULL THEN 1 END) as websiteCount, " +
           "COUNT(CASE WHEN up.linkedinUrl IS NOT NULL THEN 1 END) as linkedinCount, " +
           "COUNT(CASE WHEN JSON_LENGTH(up.certifications) > 0 THEN 1 END) as certificationCount, " +
           "COUNT(*) as totalProfiles " +
           "FROM UserProfile up JOIN up.user u WHERE u.role = 'TEACHER'")
    Object[] getProfileCompletionStats();
    
    /**
     * Get teacher performance distribution
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN up.averageScore >= 90 THEN 'Excellent' " +
           "  WHEN up.averageScore >= 80 THEN 'Good' " +
           "  WHEN up.averageScore >= 70 THEN 'Average' " +
           "  WHEN up.averageScore >= 60 THEN 'Below Average' " +
           "  ELSE 'Poor' " +
           "END as performanceCategory, " +
           "COUNT(*) as count " +
           "FROM UserProfile up JOIN up.user u " +
           "WHERE u.role = 'TEACHER' AND up.averageScore IS NOT NULL " +
           "GROUP BY performanceCategory")
    List<Object[]> getTeacherPerformanceDistribution();
}