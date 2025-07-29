package com.example.mrquiz.repository.core;

import com.example.mrquiz.entity.core.Department;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends BaseRepository<Department> {
    
    // ===== BASIC DEPARTMENT QUERIES =====
    
    /**
     * Find departments by institution
     */
    List<Department> findByInstitutionId(UUID institutionId);
    
    /**
     * Find department by institution and code
     */
    Optional<Department> findByInstitutionIdAndCode(UUID institutionId, String code);
    
    /**
     * Find root departments (no parent)
     */
    @Query("SELECT d FROM Department d WHERE d.institution.id = :institutionId AND d.parentDepartment IS NULL")
    List<Department> findRootDepartments(@Param("institutionId") UUID institutionId);
    
    /**
     * Find child departments
     */
    List<Department> findByParentDepartmentId(UUID parentDepartmentId);
    
    /**
     * Check if department code exists in institution
     */
    boolean existsByInstitutionIdAndCode(UUID institutionId, String code);
    
    // ===== HIERARCHICAL STRUCTURE =====
    
    /**
     * Find all departments in hierarchy under parent
     */
    @Query("WITH RECURSIVE dept_hierarchy AS (" +
           "  SELECT d.* FROM Department d WHERE d.id = :parentId " +
           "  UNION ALL " +
           "  SELECT d.* FROM Department d " +
           "  INNER JOIN dept_hierarchy dh ON d.parent_department_id = dh.id" +
           ") SELECT * FROM dept_hierarchy")
    List<Department> findDepartmentHierarchy(@Param("parentId") UUID parentId);
    
    /**
     * Find departments by level in hierarchy
     */
    @Query("SELECT d FROM Department d WHERE d.institution.id = :institutionId " +
           "AND (SELECT COUNT(p) FROM Department p WHERE p.id = d.parentDepartment.id) = :level")
    List<Department> findDepartmentsByLevel(@Param("institutionId") UUID institutionId, @Param("level") Integer level);
    
    /**
     * Find departments with head assigned
     */
    @Query("SELECT d FROM Department d WHERE d.institution.id = :institutionId AND d.headUser IS NOT NULL")
    List<Department> findDepartmentsWithHead(@Param("institutionId") UUID institutionId);
    
    /**
     * Find departments without head
     */
    @Query("SELECT d FROM Department d WHERE d.institution.id = :institutionId AND d.headUser IS NULL")
    List<Department> findDepartmentsWithoutHead(@Param("institutionId") UUID institutionId);
    
    // ===== DEPARTMENT MANAGEMENT =====
    
    /**
     * Find departments managed by user
     */
    @Query("SELECT d FROM Department d WHERE d.headUser.id = :userId")
    List<Department> findDepartmentsByHead(@Param("userId") UUID userId);
    
    /**
     * Count departments in institution
     */
    @Query("SELECT COUNT(d) FROM Department d WHERE d.institution.id = :institutionId")
    Long countDepartmentsByInstitution(@Param("institutionId") UUID institutionId);
    
    /**
     * Find departments with users
     */
    @Query("SELECT d FROM Department d WHERE d.institution.id = :institutionId " +
           "AND EXISTS (SELECT 1 FROM UserInstitution ui WHERE ui.department.id = d.id)")
    List<Department> findDepartmentsWithUsers(@Param("institutionId") UUID institutionId);
    
    /**
     * Find departments with courses
     */
    @Query("SELECT d FROM Department d WHERE d.institution.id = :institutionId " +
           "AND EXISTS (SELECT 1 FROM Course c WHERE c.department.id = d.id)")
    List<Department> findDepartmentsWithCourses(@Param("institutionId") UUID institutionId);
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search departments by name
     */
    @Query("SELECT d FROM Department d WHERE d.institution.id = :institutionId " +
           "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Department> searchByName(@Param("institutionId") UUID institutionId, @Param("searchTerm") String searchTerm);
    
    /**
     * Find departments by name pattern
     */
    @Query("SELECT d FROM Department d WHERE d.institution.id = :institutionId " +
           "AND d.name LIKE :pattern")
    List<Department> findByNamePattern(@Param("institutionId") UUID institutionId, @Param("pattern") String pattern);
    
    // ===== ANALYTICS AND REPORTING =====
    
    /**
     * Get department statistics
     */
    @Query("SELECT d.id, d.name, d.code, " +
           "COUNT(DISTINCT ui.id) as userCount, " +
           "COUNT(DISTINCT c.id) as courseCount, " +
           "COUNT(DISTINCT q.id) as quizCount " +
           "FROM Department d " +
           "LEFT JOIN UserInstitution ui ON d.id = ui.department.id " +
           "LEFT JOIN Course c ON d.id = c.department.id " +
           "LEFT JOIN Quiz q ON c.id = q.course.id " +
           "WHERE d.institution.id = :institutionId " +
           "GROUP BY d.id, d.name, d.code " +
           "ORDER BY userCount DESC")
    List<Object[]> getDepartmentStatistics(@Param("institutionId") UUID institutionId);
    
    /**
     * Get department user distribution
     */
    @Query("SELECT d.id, d.name, " +
           "COUNT(CASE WHEN ui.role = 'STUDENT' THEN 1 END) as studentCount, " +
           "COUNT(CASE WHEN ui.role = 'TEACHER' THEN 1 END) as teacherCount, " +
           "COUNT(CASE WHEN ui.role = 'FACULTY' THEN 1 END) as facultyCount " +
           "FROM Department d " +
           "LEFT JOIN UserInstitution ui ON d.id = ui.department.id " +
           "WHERE d.institution.id = :institutionId " +
           "GROUP BY d.id, d.name")
    List<Object[]> getDepartmentUserDistribution(@Param("institutionId") UUID institutionId);
    
    /**
     * Get department hierarchy summary
     */
    @Query("SELECT d.id, d.name, d.code, " +
           "CASE WHEN d.parentDepartment IS NULL THEN 'ROOT' ELSE d.parentDepartment.name END as parentName, " +
           "(SELECT COUNT(child) FROM Department child WHERE child.parentDepartment.id = d.id) as childCount " +
           "FROM Department d WHERE d.institution.id = :institutionId " +
           "ORDER BY d.parentDepartment.id NULLS FIRST, d.name")
    List<Object[]> getDepartmentHierarchySummary(@Param("institutionId") UUID institutionId);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update department head
     */
    @Modifying
    @Query("UPDATE Department d SET d.headUser.id = :headUserId WHERE d.id = :departmentId")
    void updateDepartmentHead(@Param("departmentId") UUID departmentId, @Param("headUserId") UUID headUserId);
    
    /**
     * Remove department head
     */
    @Modifying
    @Query("UPDATE Department d SET d.headUser = NULL WHERE d.id = :departmentId")
    void removeDepartmentHead(@Param("departmentId") UUID departmentId);
    
    /**
     * Update department settings
     */
    @Modifying
    @Query("UPDATE Department d SET d.settings = :settings WHERE d.id = :departmentId")
    void updateDepartmentSettings(@Param("departmentId") UUID departmentId, @Param("settings") String settings);
    
    /**
     * Move department to new parent
     */
    @Modifying
    @Query("UPDATE Department d SET d.parentDepartment.id = :newParentId WHERE d.id = :departmentId")
    void moveDepartment(@Param("departmentId") UUID departmentId, @Param("newParentId") UUID newParentId);
    
    /**
     * Remove department from hierarchy (make it root)
     */
    @Modifying
    @Query("UPDATE Department d SET d.parentDepartment = NULL WHERE d.id = :departmentId")
    void makeRootDepartment(@Param("departmentId") UUID departmentId);
}