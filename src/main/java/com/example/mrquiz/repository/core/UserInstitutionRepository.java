package com.example.mrquiz.repository.core;

import com.example.mrquiz.entity.core.UserInstitution;
import com.example.mrquiz.enums.InstitutionRole;
import com.example.mrquiz.enums.MembershipStatus;
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

public interface UserInstitutionRepository extends BaseRepository<UserInstitution> {
    
    // ===== BASIC RELATIONSHIP QUERIES =====
    
    /**
     * Find user-institution relationship
     */
    Optional<UserInstitution> findByUserIdAndInstitutionId(UUID userId, UUID institutionId);
    
    /**
     * Find user's institutions
     */
    List<UserInstitution> findByUserId(UUID userId);
    
    /**
     * Find institution's users
     */
    List<UserInstitution> findByInstitutionId(UUID institutionId);
    
    /**
     * Find active memberships for user
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.user.id = :userId AND ui.status = 'ACTIVE'")
    List<UserInstitution> findActiveUserMemberships(@Param("userId") UUID userId);
    
    /**
     * Find active users in institution
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId AND ui.status = 'ACTIVE'")
    List<UserInstitution> findActiveInstitutionUsers(@Param("institutionId") UUID institutionId);
    
    // ===== ROLE-BASED QUERIES =====
    
    /**
     * Find users by role in institution
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.role = :role AND ui.status = 'ACTIVE'")
    List<UserInstitution> findByInstitutionAndRole(@Param("institutionId") UUID institutionId, 
                                                  @Param("role") InstitutionRole role);
    
    /**
     * Find students in institution
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.role = 'STUDENT' AND ui.status = 'ACTIVE' ORDER BY ui.joinedAt DESC")
    Page<UserInstitution> findInstitutionStudents(@Param("institutionId") UUID institutionId, Pageable pageable);
    
    /**
     * Find teachers in institution
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.role = 'TEACHER' AND ui.status = 'ACTIVE' ORDER BY ui.joinedAt DESC")
    List<UserInstitution> findInstitutionTeachers(@Param("institutionId") UUID institutionId);
    
    /**
     * Find admins in institution
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.role = 'ADMIN' AND ui.status = 'ACTIVE'")
    List<UserInstitution> findInstitutionAdmins(@Param("institutionId") UUID institutionId);
    
    /**
     * Check if user has role in institution
     */
    @Query("SELECT COUNT(ui) > 0 FROM UserInstitution ui WHERE ui.user.id = :userId " +
           "AND ui.institution.id = :institutionId AND ui.role = :role AND ui.status = 'ACTIVE'")
    boolean hasRoleInInstitution(@Param("userId") UUID userId, 
                                @Param("institutionId") UUID institutionId, 
                                @Param("role") InstitutionRole role);
    
    // ===== DEPARTMENT-BASED QUERIES =====
    
    /**
     * Find users in department
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.department.id = :departmentId " +
           "AND ui.status = 'ACTIVE' ORDER BY ui.role, ui.joinedAt")
    List<UserInstitution> findByDepartment(@Param("departmentId") UUID departmentId);
    
    /**
     * Find students in department
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.department.id = :departmentId " +
           "AND ui.role = 'STUDENT' AND ui.status = 'ACTIVE'")
    List<UserInstitution> findDepartmentStudents(@Param("departmentId") UUID departmentId);
    
    /**
     * Find faculty in department
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.department.id = :departmentId " +
           "AND ui.role IN ('TEACHER', 'FACULTY') AND ui.status = 'ACTIVE'")
    List<UserInstitution> findDepartmentFaculty(@Param("departmentId") UUID departmentId);
    
    // ===== STATUS AND LIFECYCLE MANAGEMENT =====
    
    /**
     * Find memberships by status
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.status = :status ORDER BY ui.joinedAt DESC")
    List<UserInstitution> findByInstitutionAndStatus(@Param("institutionId") UUID institutionId, 
                                                    @Param("status") MembershipStatus status);
    
    /**
     * Find graduated students
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.role = 'STUDENT' AND ui.status = 'GRADUATED' ORDER BY ui.leftAt DESC")
    List<UserInstitution> findGraduatedStudents(@Param("institutionId") UUID institutionId);
    
    /**
     * Find transferred students
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.role = 'STUDENT' AND ui.status = 'TRANSFERRED' ORDER BY ui.leftAt DESC")
    List<UserInstitution> findTransferredStudents(@Param("institutionId") UUID institutionId);
    
    /**
     * Find inactive memberships
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.status = 'INACTIVE' ORDER BY ui.leftAt DESC")
    List<UserInstitution> findInactiveMemberships(@Param("institutionId") UUID institutionId);
    
    // ===== STUDENT AND EMPLOYEE ID MANAGEMENT =====
    
    /**
     * Find by student ID
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.studentId = :studentId")
    Optional<UserInstitution> findByInstitutionAndStudentId(@Param("institutionId") UUID institutionId, 
                                                           @Param("studentId") String studentId);
    
    /**
     * Find by employee ID
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.employeeId = :employeeId")
    Optional<UserInstitution> findByInstitutionAndEmployeeId(@Param("institutionId") UUID institutionId, 
                                                            @Param("employeeId") String employeeId);
    
    /**
     * Check if student ID exists
     */
    @Query("SELECT COUNT(ui) > 0 FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.studentId = :studentId")
    boolean existsByInstitutionAndStudentId(@Param("institutionId") UUID institutionId, 
                                           @Param("studentId") String studentId);
    
    /**
     * Check if employee ID exists
     */
    @Query("SELECT COUNT(ui) > 0 FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND ui.employeeId = :employeeId")
    boolean existsByInstitutionAndEmployeeId(@Param("institutionId") UUID institutionId, 
                                            @Param("employeeId") String employeeId);
    
    // ===== ANALYTICS AND REPORTING =====
    
    /**
     * Count users by role in institution
     */
    @Query("SELECT ui.role, COUNT(ui) FROM UserInstitution ui " +
           "WHERE ui.institution.id = :institutionId AND ui.status = 'ACTIVE' " +
           "GROUP BY ui.role")
    List<Object[]> countUsersByRole(@Param("institutionId") UUID institutionId);
    
    /**
     * Count users by status in institution
     */
    @Query("SELECT ui.status, COUNT(ui) FROM UserInstitution ui " +
           "WHERE ui.institution.id = :institutionId GROUP BY ui.status")
    List<Object[]> countUsersByStatus(@Param("institutionId") UUID institutionId);
    
    /**
     * Count users by department
     */
    @Query("SELECT d.name, COUNT(ui) as userCount FROM UserInstitution ui " +
           "RIGHT JOIN Department d ON ui.department.id = d.id " +
           "WHERE d.institution.id = :institutionId AND ui.status = 'ACTIVE' " +
           "GROUP BY d.id, d.name ORDER BY userCount DESC")
    List<Object[]> countUsersByDepartment(@Param("institutionId") UUID institutionId);
    
    /**
     * Get membership statistics for period
     */
    @Query("SELECT DATE(ui.joinedAt) as date, " +
           "COUNT(ui) as newMemberships, " +
           "COUNT(CASE WHEN ui.leftAt IS NOT NULL THEN 1 END) as leftMemberships " +
           "FROM UserInstitution ui " +
           "WHERE ui.institution.id = :institutionId " +
           "AND ui.joinedAt BETWEEN :start AND :end " +
           "GROUP BY DATE(ui.joinedAt) ORDER BY date")
    List<Object[]> getMembershipStatsForPeriod(@Param("institutionId") UUID institutionId,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);
    
    /**
     * Get user retention statistics
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN ui.status = 'ACTIVE' THEN 1 END) as activeUsers, " +
           "COUNT(CASE WHEN ui.status = 'GRADUATED' THEN 1 END) as graduatedUsers, " +
           "COUNT(CASE WHEN ui.status = 'TRANSFERRED' THEN 1 END) as transferredUsers, " +
           "COUNT(CASE WHEN ui.status = 'INACTIVE' THEN 1 END) as inactiveUsers, " +
           "AVG(CASE WHEN ui.leftAt IS NOT NULL THEN " +
           "    EXTRACT(EPOCH FROM (ui.leftAt - ui.joinedAt))/86400 END) as avgDaysActive " +
           "FROM UserInstitution ui WHERE ui.institution.id = :institutionId")
    Object[] getUserRetentionStats(@Param("institutionId") UUID institutionId);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update user role in institution
     */
    @Modifying
    @Query("UPDATE UserInstitution ui SET ui.role = :role WHERE ui.user.id = :userId " +
           "AND ui.institution.id = :institutionId")
    void updateUserRole(@Param("userId") UUID userId, 
                       @Param("institutionId") UUID institutionId, 
                       @Param("role") InstitutionRole role);
    
    /**
     * Update membership status
     */
    @Modifying
    @Query("UPDATE UserInstitution ui SET ui.status = :status, ui.leftAt = :leftAt " +
           "WHERE ui.user.id = :userId AND ui.institution.id = :institutionId")
    void updateMembershipStatus(@Param("userId") UUID userId, 
                               @Param("institutionId") UUID institutionId, 
                               @Param("status") MembershipStatus status,
                               @Param("leftAt") LocalDateTime leftAt);
    
    /**
     * Update user department
     */
    @Modifying
    @Query("UPDATE UserInstitution ui SET ui.department.id = :departmentId " +
           "WHERE ui.user.id = :userId AND ui.institution.id = :institutionId")
    void updateUserDepartment(@Param("userId") UUID userId, 
                             @Param("institutionId") UUID institutionId, 
                             @Param("departmentId") UUID departmentId);
    
    /**
     * Update student ID
     */
    @Modifying
    @Query("UPDATE UserInstitution ui SET ui.studentId = :studentId " +
           "WHERE ui.user.id = :userId AND ui.institution.id = :institutionId")
    void updateStudentId(@Param("userId") UUID userId, 
                        @Param("institutionId") UUID institutionId, 
                        @Param("studentId") String studentId);
    
    /**
     * Update employee ID
     */
    @Modifying
    @Query("UPDATE UserInstitution ui SET ui.employeeId = :employeeId " +
           "WHERE ui.user.id = :userId AND ui.institution.id = :institutionId")
    void updateEmployeeId(@Param("userId") UUID userId, 
                         @Param("institutionId") UUID institutionId, 
                         @Param("employeeId") String employeeId);
    
    /**
     * Bulk update status for users
     */
    @Modifying
    @Query("UPDATE UserInstitution ui SET ui.status = :status, ui.leftAt = :leftAt " +
           "WHERE ui.institution.id = :institutionId AND ui.user.id IN :userIds")
    void bulkUpdateStatus(@Param("institutionId") UUID institutionId, 
                         @Param("userIds") List<UUID> userIds, 
                         @Param("status") MembershipStatus status,
                         @Param("leftAt") LocalDateTime leftAt);
    
    // ===== PERMISSION AND ACCESS CONTROL =====
    
    /**
     * Find users with specific permission
     */
    @Query("SELECT ui FROM UserInstitution ui WHERE ui.institution.id = :institutionId " +
           "AND JSON_EXTRACT(ui.permissions, CONCAT('$.', :permission)) = true " +
           "AND ui.status = 'ACTIVE'")
    List<UserInstitution> findUsersWithPermission(@Param("institutionId") UUID institutionId, 
                                                  @Param("permission") String permission);
    
    /**
     * Check if user has permission
     */
    @Query("SELECT COUNT(ui) > 0 FROM UserInstitution ui WHERE ui.user.id = :userId " +
           "AND ui.institution.id = :institutionId " +
           "AND JSON_EXTRACT(ui.permissions, CONCAT('$.', :permission)) = true " +
           "AND ui.status = 'ACTIVE'")
    boolean hasPermission(@Param("userId") UUID userId, 
                         @Param("institutionId") UUID institutionId, 
                         @Param("permission") String permission);
    
    /**
     * Update user permissions
     */
    @Modifying
    @Query("UPDATE UserInstitution ui SET ui.permissions = :permissions " +
           "WHERE ui.user.id = :userId AND ui.institution.id = :institutionId")
    void updatePermissions(@Param("userId") UUID userId, 
                          @Param("institutionId") UUID institutionId, 
                          @Param("permissions") String permissions);
}