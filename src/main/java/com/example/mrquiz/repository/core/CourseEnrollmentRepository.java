package com.example.mrquiz.repository.core;

import com.example.mrquiz.entity.core.CourseEnrollment;
import com.example.mrquiz.enums.EnrollmentStatus;
import com.example.mrquiz.enums.EnrollmentType;
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

public interface CourseEnrollmentRepository extends BaseRepository<CourseEnrollment> {
    
    // ===== BASIC ENROLLMENT QUERIES =====
    
    /**
     * Find enrollment by user and course
     */
    Optional<CourseEnrollment> findByUserIdAndCourseId(UUID userId, UUID courseId);
    
    /**
     * Find enrollments by user
     */
    List<CourseEnrollment> findByUserId(UUID userId);
    
    /**
     * Find enrollments by course
     */
    List<CourseEnrollment> findByCourseId(UUID courseId);
    
    /**
     * Find enrollments by status
     */
    List<CourseEnrollment> findByStatus(EnrollmentStatus status);
    
    /**
     * Find enrollments by type
     */
    List<CourseEnrollment> findByEnrollmentType(EnrollmentType enrollmentType);
    
    /**
     * Check if user is enrolled in course
     */
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);
    
    // ===== ACTIVE ENROLLMENT MANAGEMENT =====
    
    /**
     * Find active enrollments for user
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.user.id = :userId AND ce.status = 'ACTIVE' " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findActiveEnrollmentsForUser(@Param("userId") UUID userId);
    
    /**
     * Find active enrollments for course
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.course.id = :courseId AND ce.status = 'ACTIVE' " +
           "ORDER BY ce.enrolledAt DESC")
    Page<CourseEnrollment> findActiveEnrollmentsForCourse(@Param("courseId") UUID courseId, Pageable pageable);
    
    /**
     * Find active student enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.status = 'ACTIVE' " +
           "AND ce.course.status = 'ACTIVE' ORDER BY ce.enrolledAt DESC")
    Page<CourseEnrollment> findActiveStudentEnrollments(Pageable pageable);
    
    /**
     * Count active enrollments for course
     */
    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = :courseId AND ce.status = 'ACTIVE'")
    Long countActiveEnrollmentsForCourse(@Param("courseId") UUID courseId);
    
    // ===== ENROLLMENT TYPE MANAGEMENT =====
    
    /**
     * Find regular enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.enrollmentType = 'REGULAR' AND ce.status = 'ACTIVE' " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findRegularEnrollments();
    
    /**
     * Find audit enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.enrollmentType = 'AUDIT' AND ce.status = 'ACTIVE' " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findAuditEnrollments();
    
    /**
     * Find credit enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.enrollmentType = 'CREDIT' AND ce.status = 'ACTIVE' " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findCreditEnrollments();
    
    /**
     * Find guest enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.enrollmentType = 'GUEST' AND ce.status = 'ACTIVE' " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findGuestEnrollments();
    
    // ===== ENROLLMENT STATUS TRACKING =====
    
    /**
     * Find completed enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.status = 'COMPLETED' " +
           "ORDER BY ce.completedAt DESC")
    Page<CourseEnrollment> findCompletedEnrollments(Pageable pageable);
    
    /**
     * Find dropped enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.status = 'DROPPED' " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findDroppedEnrollments();
    
    /**
     * Find withdrawn enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.status = 'WITHDRAWN' " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findWithdrawnEnrollments();
    
    /**
     * Find failed enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.status = 'FAILED' " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findFailedEnrollments();
    
    // ===== GRADING AND PERFORMANCE =====
    
    /**
     * Find enrollments with grades
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.finalGrade IS NOT NULL " +
           "ORDER BY ce.finalGrade DESC")
    List<CourseEnrollment> findEnrollmentsWithGrades();
    
    /**
     * Find enrollments by grade range
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.finalGrade BETWEEN :minGrade AND :maxGrade " +
           "ORDER BY ce.finalGrade DESC")
    List<CourseEnrollment> findEnrollmentsByGradeRange(@Param("minGrade") BigDecimal minGrade,
                                                      @Param("maxGrade") BigDecimal maxGrade);
    
    /**
     * Find high-performing students
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.finalGrade >= :gradeThreshold " +
           "AND ce.status = 'COMPLETED' ORDER BY ce.finalGrade DESC")
    List<CourseEnrollment> findHighPerformingStudents(@Param("gradeThreshold") BigDecimal gradeThreshold);
    
    /**
     * Find students needing help
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.finalGrade < :gradeThreshold " +
           "AND ce.status = 'ACTIVE' ORDER BY ce.finalGrade ASC")
    List<CourseEnrollment> findStudentsNeedingHelp(@Param("gradeThreshold") BigDecimal gradeThreshold);
    
    /**
     * Find enrollments by letter grade
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.gradeLetter = :letterGrade " +
           "ORDER BY ce.finalGrade DESC")
    List<CourseEnrollment> findEnrollmentsByLetterGrade(@Param("letterGrade") String letterGrade);
    
    // ===== INSTRUCTOR COURSE MANAGEMENT =====
    
    /**
     * Find enrollments for instructor's courses
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.course.instructor.id = :instructorId " +
           "AND ce.status = 'ACTIVE' ORDER BY ce.course.name, ce.user.lastName")
    Page<CourseEnrollment> findEnrollmentsForInstructor(@Param("instructorId") UUID instructorId, Pageable pageable);
    
    /**
     * Get instructor's course enrollment summary
     */
    @Query("SELECT c.id, c.name, c.code, " +
           "COUNT(ce) as totalEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'ACTIVE' THEN 1 END) as activeEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'COMPLETED' THEN 1 END) as completedEnrollments, " +
           "AVG(ce.finalGrade) as averageGrade " +
           "FROM CourseEnrollment ce JOIN Course c ON ce.course.id = c.id " +
           "WHERE c.instructor.id = :instructorId " +
           "GROUP BY c.id, c.name, c.code ORDER BY activeEnrollments DESC")
    List<Object[]> getInstructorEnrollmentSummary(@Param("instructorId") UUID instructorId);
    
    /**
     * Find instructor's students across all courses
     */
    @Query("SELECT DISTINCT ce.user FROM CourseEnrollment ce " +
           "WHERE ce.course.instructor.id = :instructorId AND ce.status = 'ACTIVE'")
    List<Object> findInstructorStudents(@Param("instructorId") UUID instructorId);
    
    // ===== STUDENT ACADEMIC HISTORY =====
    
    /**
     * Find student's academic history
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.user.id = :studentId " +
           "ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findStudentAcademicHistory(@Param("studentId") UUID studentId);
    
    /**
     * Find student's current enrollments
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.user.id = :studentId " +
           "AND ce.status = 'ACTIVE' AND ce.course.status = 'ACTIVE' " +
           "ORDER BY ce.course.name")
    List<CourseEnrollment> findStudentCurrentEnrollments(@Param("studentId") UUID studentId);
    
    /**
     * Calculate student's GPA
     */
    @Query("SELECT AVG(ce.gpaPoints) FROM CourseEnrollment ce WHERE ce.user.id = :studentId " +
           "AND ce.gpaPoints IS NOT NULL AND ce.status = 'COMPLETED'")
    Optional<BigDecimal> calculateStudentGPA(@Param("studentId") UUID studentId);
    
    /**
     * Count student's completed courses
     */
    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.user.id = :studentId " +
           "AND ce.status = 'COMPLETED'")
    Long countStudentCompletedCourses(@Param("studentId") UUID studentId);
    
    // ===== ENROLLMENT ANALYTICS =====
    
    /**
     * Get enrollment statistics by course
     */
    @Query("SELECT c.id, c.name, c.code, " +
           "COUNT(ce) as totalEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'ACTIVE' THEN 1 END) as activeEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'COMPLETED' THEN 1 END) as completions, " +
           "COUNT(CASE WHEN ce.status = 'DROPPED' THEN 1 END) as dropouts, " +
           "(COUNT(CASE WHEN ce.status = 'COMPLETED' THEN 1 END) * 100.0 / NULLIF(COUNT(ce), 0)) as completionRate " +
           "FROM CourseEnrollment ce JOIN Course c ON ce.course.id = c.id " +
           "WHERE c.institution.id = :institutionId " +
           "GROUP BY c.id, c.name, c.code ORDER BY totalEnrollments DESC")
    List<Object[]> getEnrollmentStatisticsByCourse(@Param("institutionId") UUID institutionId);
    
    /**
     * Get enrollment trends over time
     */
    @Query("SELECT DATE(ce.enrolledAt) as date, COUNT(ce) as enrollments " +
           "FROM CourseEnrollment ce WHERE ce.enrolledAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(ce.enrolledAt) ORDER BY date")
    List<Object[]> getEnrollmentTrends(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get grade distribution for course
     */
    @Query("SELECT ce.gradeLetter, COUNT(ce) as count FROM CourseEnrollment ce " +
           "WHERE ce.course.id = :courseId AND ce.gradeLetter IS NOT NULL " +
           "GROUP BY ce.gradeLetter ORDER BY ce.gradeLetter")
    List<Object[]> getGradeDistributionForCourse(@Param("courseId") UUID courseId);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update enrollment status
     */
    @Modifying
    @Query("UPDATE CourseEnrollment ce SET ce.status = :status WHERE ce.id = :enrollmentId")
    void updateEnrollmentStatus(@Param("enrollmentId") UUID enrollmentId, @Param("status") EnrollmentStatus status);
    
    /**
     * Update final grade
     */
    @Modifying
    @Query("UPDATE CourseEnrollment ce SET ce.finalGrade = :grade, ce.gradeLetter = :letterGrade, " +
           "ce.gpaPoints = :gpaPoints WHERE ce.id = :enrollmentId")
    void updateFinalGrade(@Param("enrollmentId") UUID enrollmentId,
                         @Param("grade") BigDecimal grade,
                         @Param("letterGrade") String letterGrade,
                         @Param("gpaPoints") BigDecimal gpaPoints);
    
    /**
     * Mark enrollment as completed
     */
    @Modifying
    @Query("UPDATE CourseEnrollment ce SET ce.status = 'COMPLETED', ce.completedAt = :completedAt " +
           "WHERE ce.id = :enrollmentId")
    void markAsCompleted(@Param("enrollmentId") UUID enrollmentId, @Param("completedAt") LocalDateTime completedAt);
    
    /**
     * Drop enrollment
     */
    @Modifying
    @Query("UPDATE CourseEnrollment ce SET ce.status = 'DROPPED' WHERE ce.id = :enrollmentId")
    void dropEnrollment(@Param("enrollmentId") UUID enrollmentId);
    
    /**
     * Bulk update enrollment type
     */
    @Modifying
    @Query("UPDATE CourseEnrollment ce SET ce.enrollmentType = :enrollmentType " +
           "WHERE ce.course.id = :courseId AND ce.status = 'ACTIVE'")
    void bulkUpdateEnrollmentType(@Param("courseId") UUID courseId, @Param("enrollmentType") EnrollmentType enrollmentType);
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search enrollments with filters
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE " +
           "(:courseId IS NULL OR ce.course.id = :courseId) " +
           "AND (:userId IS NULL OR ce.user.id = :userId) " +
           "AND (:status IS NULL OR ce.status = :status) " +
           "AND (:enrollmentType IS NULL OR ce.enrollmentType = :enrollmentType) " +
           "AND (:instructorId IS NULL OR ce.course.instructor.id = :instructorId) " +
           "ORDER BY ce.enrolledAt DESC")
    Page<CourseEnrollment> searchEnrollmentsWithFilters(@Param("courseId") UUID courseId,
                                                       @Param("userId") UUID userId,
                                                       @Param("status") EnrollmentStatus status,
                                                       @Param("enrollmentType") EnrollmentType enrollmentType,
                                                       @Param("instructorId") UUID instructorId,
                                                       Pageable pageable);
    
    /**
     * Find enrollments by student name
     */
    @Query("SELECT ce FROM CourseEnrollment ce WHERE " +
           "LOWER(CONCAT(ce.user.firstName, ' ', ce.user.lastName)) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY ce.user.lastName, ce.user.firstName")
    List<CourseEnrollment> findEnrollmentsByStudentName(@Param("name") String name);
    
    // ===== REPORTING QUERIES =====
    
    /**
     * Count enrollments by status
     */
    @Query("SELECT ce.status, COUNT(ce) FROM CourseEnrollment ce GROUP BY ce.status")
    List<Object[]> countEnrollmentsByStatus();
    
    /**
     * Count enrollments by type
     */
    @Query("SELECT ce.enrollmentType, COUNT(ce) FROM CourseEnrollment ce GROUP BY ce.enrollmentType")
    List<Object[]> countEnrollmentsByType();
    
    /**
     * Get enrollment retention rate
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN ce.status = 'ACTIVE' THEN 1 END) as activeEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'COMPLETED' THEN 1 END) as completedEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'DROPPED' THEN 1 END) as droppedEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'WITHDRAWN' THEN 1 END) as withdrawnEnrollments, " +
           "(COUNT(CASE WHEN ce.status IN ('ACTIVE', 'COMPLETED') THEN 1 END) * 100.0 / COUNT(ce)) as retentionRate " +
           "FROM CourseEnrollment ce WHERE ce.course.id = :courseId")
    Object[] getEnrollmentRetentionRate(@Param("courseId") UUID courseId);
    
    /**
     * Get most popular courses by enrollment count
     */
    @Query("SELECT c.id, c.name, c.code, COUNT(ce) as enrollmentCount " +
           "FROM CourseEnrollment ce JOIN Course c ON ce.course.id = c.id " +
           "WHERE ce.status = 'ACTIVE' " +
           "GROUP BY c.id, c.name, c.code ORDER BY enrollmentCount DESC")
    List<Object[]> getMostPopularCoursesByEnrollment(Pageable pageable);
}