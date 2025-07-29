package com.example.mrquiz.repository.core;

import com.example.mrquiz.entity.core.Course;
import com.example.mrquiz.enums.CourseStatus;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends BaseRepository<Course> {
    
    // ===== BASIC COURSE QUERIES =====
    
    /**
     * Find courses by institution
     */
    List<Course> findByInstitutionId(UUID institutionId);
    
    /**
     * Find courses by department
     */
    List<Course> findByDepartmentId(UUID departmentId);
    
    /**
     * Find courses by instructor
     */
    List<Course> findByInstructorId(UUID instructorId);
    
    /**
     * Find course by institution and code
     */
    Optional<Course> findByInstitutionIdAndCode(UUID institutionId, String code);
    
    /**
     * Find courses by status
     */
    List<Course> findByStatus(CourseStatus status);
    
    /**
     * Check if course code exists in institution
     */
    boolean existsByInstitutionIdAndCode(UUID institutionId, String code);
    
    // ===== ACTIVE COURSE MANAGEMENT =====
    
    /**
     * Find active courses
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' ORDER BY c.name ASC")
    List<Course> findActiveCourses();
    
    /**
     * Find active courses by institution
     */
    @Query("SELECT c FROM Course c WHERE c.institution.id = :institutionId AND c.status = 'ACTIVE' " +
           "ORDER BY c.name ASC")
    List<Course> findActiveCoursesInInstitution(@Param("institutionId") UUID institutionId);
    
    /**
     * Find active courses by department
     */
    @Query("SELECT c FROM Course c WHERE c.department.id = :departmentId AND c.status = 'ACTIVE' " +
           "ORDER BY c.name ASC")
    List<Course> findActiveCoursesByDepartment(@Param("departmentId") UUID departmentId);
    
    /**
     * Find courses taught by instructor
     */
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId AND c.status = 'ACTIVE' " +
           "ORDER BY c.startDate DESC")
    Page<Course> findActiveCoursesForInstructor(@Param("instructorId") UUID instructorId, Pageable pageable);
    
    // ===== ACADEMIC TERM MANAGEMENT =====
    
    /**
     * Find courses by semester and academic year
     */
    @Query("SELECT c FROM Course c WHERE c.semester = :semester AND c.academicYear = :academicYear " +
           "ORDER BY c.name ASC")
    List<Course> findBySemesterAndYear(@Param("semester") String semester, @Param("academicYear") String academicYear);
    
    /**
     * Find current semester courses
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' " +
           "AND (c.startDate IS NULL OR c.startDate <= :now) " +
           "AND (c.endDate IS NULL OR c.endDate >= :now) " +
           "ORDER BY c.name ASC")
    List<Course> findCurrentSemesterCourses(@Param("now") LocalDate now);
    
    /**
     * Find upcoming courses
     */
    @Query("SELECT c FROM Course c WHERE c.status IN ('ACTIVE', 'SCHEDULED') " +
           "AND c.startDate > :now ORDER BY c.startDate ASC")
    List<Course> findUpcomingCourses(@Param("now") LocalDate now);
    
    /**
     * Find courses ending soon
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' " +
           "AND c.endDate BETWEEN :now AND :threshold ORDER BY c.endDate ASC")
    List<Course> findCoursesEndingSoon(@Param("now") LocalDate now, @Param("threshold") LocalDate threshold);
    
    // ===== ENROLLMENT AND CAPACITY =====
    
    /**
     * Find courses with available spots
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' " +
           "AND (SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = c.id AND ce.status = 'ACTIVE') " +
           "< CAST(JSON_EXTRACT(c.settings, '$.maxEnrollment') AS INTEGER) " +
           "ORDER BY c.name ASC")
    List<Course> findCoursesWithAvailableSpots();
    
    /**
     * Find courses at capacity
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' " +
           "AND (SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = c.id AND ce.status = 'ACTIVE') " +
           ">= CAST(JSON_EXTRACT(c.settings, '$.maxEnrollment') AS INTEGER) " +
           "ORDER BY c.name ASC")
    List<Course> findCoursesAtCapacity();
    
    /**
     * Get course enrollment statistics
     */
    @Query("SELECT c.id, c.name, c.code, " +
           "COUNT(ce) as totalEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'ACTIVE' THEN 1 END) as activeEnrollments, " +
           "COUNT(CASE WHEN ce.status = 'COMPLETED' THEN 1 END) as completedEnrollments " +
           "FROM Course c LEFT JOIN CourseEnrollment ce ON c.id = ce.course.id " +
           "WHERE c.institution.id = :institutionId " +
           "GROUP BY c.id, c.name, c.code ORDER BY activeEnrollments DESC")
    List<Object[]> getCourseEnrollmentStats(@Param("institutionId") UUID institutionId);
    
    // ===== COURSE LEVEL AND PREREQUISITES =====
    
    /**
     * Find courses by level
     */
    @Query("SELECT c FROM Course c WHERE c.level = :level AND c.status = 'ACTIVE' ORDER BY c.name ASC")
    List<Course> findCoursesByLevel(@Param("level") String level);
    
    /**
     * Find undergraduate courses
     */
    @Query("SELECT c FROM Course c WHERE c.level = 'undergraduate' AND c.status = 'ACTIVE' " +
           "ORDER BY c.name ASC")
    List<Course> findUndergraduateCourses();
    
    /**
     * Find graduate courses
     */
    @Query("SELECT c FROM Course c WHERE c.level = 'graduate' AND c.status = 'ACTIVE' " +
           "ORDER BY c.name ASC")
    List<Course> findGraduateCourses();
    
    /**
     * Find courses with prerequisites
     */
    @Query("SELECT c FROM Course c WHERE JSON_LENGTH(c.prerequisites) > 0 AND c.status = 'ACTIVE'")
    List<Course> findCoursesWithPrerequisites();
    
    /**
     * Find courses without prerequisites
     */
    @Query("SELECT c FROM Course c WHERE JSON_LENGTH(c.prerequisites) = 0 AND c.status = 'ACTIVE' " +
           "ORDER BY c.name ASC")
    List<Course> findCoursesWithoutPrerequisites();
    
    // ===== INSTRUCTOR MANAGEMENT =====
    
    /**
     * Find courses without instructor
     */
    @Query("SELECT c FROM Course c WHERE c.instructor IS NULL AND c.status IN ('ACTIVE', 'SCHEDULED') " +
           "ORDER BY c.startDate ASC")
    List<Course> findCoursesWithoutInstructor();
    
    /**
     * Find courses with teaching assistants
     */
    @Query("SELECT c FROM Course c WHERE JSON_LENGTH(c.teachingAssistants) > 0 AND c.status = 'ACTIVE'")
    List<Course> findCoursesWithTAs();
    
    /**
     * Count courses by instructor
     */
    @Query("SELECT u.email, COUNT(c) as courseCount FROM Course c " +
           "JOIN User u ON c.instructor.id = u.id " +
           "WHERE c.status = 'ACTIVE' " +
           "GROUP BY u.id, u.email ORDER BY courseCount DESC")
    List<Object[]> countCoursesByInstructor();
    
    /**
     * Find instructor's course load
     */
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId " +
           "AND c.status = 'ACTIVE' AND c.semester = :semester AND c.academicYear = :academicYear")
    List<Course> getInstructorCourseLoad(@Param("instructorId") UUID instructorId,
                                        @Param("semester") String semester,
                                        @Param("academicYear") String academicYear);
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search courses by name
     */
    @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND c.status = 'ACTIVE' ORDER BY c.name ASC")
    Page<Course> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Search courses by code
     */
    @Query("SELECT c FROM Course c WHERE LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND c.status = 'ACTIVE' ORDER BY c.code ASC")
    List<Course> searchByCode(@Param("searchTerm") String searchTerm);
    
    /**
     * Advanced course search
     */
    @Query("SELECT c FROM Course c WHERE " +
           "(:institutionId IS NULL OR c.institution.id = :institutionId) " +
           "AND (:departmentId IS NULL OR c.department.id = :departmentId) " +
           "AND (:instructorId IS NULL OR c.instructor.id = :instructorId) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:level IS NULL OR c.level = :level) " +
           "AND (:semester IS NULL OR c.semester = :semester) " +
           "AND (:academicYear IS NULL OR c.academicYear = :academicYear) " +
           "AND (:searchTerm IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "     OR LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY c.name ASC")
    Page<Course> searchCoursesWithFilters(@Param("institutionId") UUID institutionId,
                                         @Param("departmentId") UUID departmentId,
                                         @Param("instructorId") UUID instructorId,
                                         @Param("status") CourseStatus status,
                                         @Param("level") String level,
                                         @Param("semester") String semester,
                                         @Param("academicYear") String academicYear,
                                         @Param("searchTerm") String searchTerm,
                                         Pageable pageable);
    
    // ===== QUIZ AND ASSESSMENT INTEGRATION =====
    
    /**
     * Find courses with quizzes
     */
    @Query("SELECT DISTINCT c FROM Course c WHERE EXISTS " +
           "(SELECT 1 FROM Quiz q WHERE q.course.id = c.id) AND c.status = 'ACTIVE'")
    List<Course> findCoursesWithQuizzes();
    
    /**
     * Get course quiz statistics
     */
    @Query("SELECT c.id, c.name, c.code, " +
           "COUNT(q) as totalQuizzes, " +
           "COUNT(CASE WHEN q.status = 'PUBLISHED' THEN 1 END) as publishedQuizzes, " +
           "COUNT(CASE WHEN q.status = 'DRAFT' THEN 1 END) as draftQuizzes " +
           "FROM Course c LEFT JOIN Quiz q ON c.id = q.course.id " +
           "WHERE c.instructor.id = :instructorId AND c.status = 'ACTIVE' " +
           "GROUP BY c.id, c.name, c.code")
    List<Object[]> getCourseQuizStats(@Param("instructorId") UUID instructorId);
    
    /**
     * Find courses with recent quiz activity
     */
    @Query("SELECT DISTINCT c FROM Course c JOIN Quiz q ON c.id = q.course.id " +
           "WHERE q.updatedAt > :since AND c.status = 'ACTIVE' ORDER BY c.name ASC")
    List<Course> findCoursesWithRecentQuizActivity(@Param("since") LocalDateTime since);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update course status
     */
    @Modifying
    @Query("UPDATE Course c SET c.status = :status WHERE c.id = :courseId")
    void updateCourseStatus(@Param("courseId") UUID courseId, @Param("status") CourseStatus status);
    
    /**
     * Update course instructor
     */
    @Modifying
    @Query("UPDATE Course c SET c.instructor.id = :instructorId WHERE c.id = :courseId")
    void updateCourseInstructor(@Param("courseId") UUID courseId, @Param("instructorId") UUID instructorId);
    
    /**
     * Update course settings
     */
    @Modifying
    @Query("UPDATE Course c SET c.settings = :settings WHERE c.id = :courseId")
    void updateCourseSettings(@Param("courseId") UUID courseId, @Param("settings") String settings);
    
    /**
     * Add teaching assistant
     */
    @Modifying
    @Query("UPDATE Course c SET c.teachingAssistants = JSON_ARRAY_APPEND(c.teachingAssistants, '$', :taId) " +
           "WHERE c.id = :courseId")
    void addTeachingAssistant(@Param("courseId") UUID courseId, @Param("taId") UUID taId);
    
    /**
     * Archive old courses
     */
    @Modifying
    @Query("UPDATE Course c SET c.status = 'ARCHIVED' WHERE c.endDate < :cutoffDate AND c.status = 'ACTIVE'")
    void archiveOldCourses(@Param("cutoffDate") LocalDate cutoffDate);
    
    // ===== ANALYTICS AND REPORTING =====
    
    /**
     * Count courses by status
     */
    @Query("SELECT c.status, COUNT(c) FROM Course c GROUP BY c.status")
    List<Object[]> countCoursesByStatus();
    
    /**
     * Count courses by level
     */
    @Query("SELECT c.level, COUNT(c) FROM Course c WHERE c.status = 'ACTIVE' GROUP BY c.level")
    List<Object[]> countCoursesByLevel();
    
    /**
     * Count courses by semester
     */
    @Query("SELECT c.semester, c.academicYear, COUNT(c) as courseCount FROM Course c " +
           "WHERE c.status = 'ACTIVE' GROUP BY c.semester, c.academicYear " +
           "ORDER BY c.academicYear DESC, c.semester")
    List<Object[]> countCoursesBySemester();
    
    /**
     * Get department course distribution
     */
    @Query("SELECT d.name, COUNT(c) as courseCount FROM Course c " +
           "JOIN Department d ON c.department.id = d.id " +
           "WHERE c.status = 'ACTIVE' " +
           "GROUP BY d.id, d.name ORDER BY courseCount DESC")
    List<Object[]> getDepartmentCourseDistribution();
    
    /**
     * Get course creation statistics
     */
    @Query("SELECT DATE(c.createdAt) as date, COUNT(c) as count FROM Course c " +
           "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(c.createdAt) ORDER BY date")
    List<Object[]> getCourseCreationStats(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get most popular courses
     */
    @Query("SELECT c.id, c.name, c.code, COUNT(ce) as enrollmentCount " +
           "FROM Course c LEFT JOIN CourseEnrollment ce ON c.id = ce.course.id " +
           "WHERE c.status = 'ACTIVE' AND ce.status = 'ACTIVE' " +
           "GROUP BY c.id, c.name, c.code ORDER BY enrollmentCount DESC")
    List<Object[]> getMostPopularCourses(Pageable pageable);
    
    /**
     * Get course performance summary
     */
    @Query("SELECT c.id, c.name, c.code, " +
           "COUNT(DISTINCT ce.id) as totalStudents, " +
           "COUNT(DISTINCT q.id) as totalQuizzes, " +
           "COUNT(DISTINCT qa.id) as totalAttempts, " +
           "AVG(qa.percentage) as avgScore " +
           "FROM Course c " +
           "LEFT JOIN CourseEnrollment ce ON c.id = ce.course.id " +
           "LEFT JOIN Quiz q ON c.id = q.course.id " +
           "LEFT JOIN QuizAttempt qa ON q.id = qa.quiz.id " +
           "WHERE c.instructor.id = :instructorId AND c.status = 'ACTIVE' " +
           "GROUP BY c.id, c.name, c.code")
    List<Object[]> getCoursePerformanceSummary(@Param("instructorId") UUID instructorId);
}