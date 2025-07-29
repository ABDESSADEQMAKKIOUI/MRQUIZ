# Complete SmartQuiz Repository Layer Documentation

## Overview

This document provides a comprehensive overview of all repositories created for the SmartQuiz platform. The repository layer implements a complete data access layer with advanced querying capabilities, supporting all the requested functionality including multi-institutional support, real-time features, analytics, security, and file management.

## Repository Architecture

### Base Repository Pattern
All repositories extend `BaseRepository<T>` which provides:
- Standard JPA operations (`JpaRepository`)
- Dynamic query building (`JpaSpecificationExecutor`)
- UUID-based primary keys
- Common CRUD operations

```java
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
}
```

### Package Structure
```
src/main/java/com/example/mrquiz/repository/
├── BaseRepository.java
├── auth/
│   ├── UserRepository.java
│   ├── UserProfileRepository.java
│   └── AuthTokenRepository.java
├── core/
│   ├── InstitutionRepository.java
│   ├── DepartmentRepository.java
│   ├── UserInstitutionRepository.java
│   ├── CourseRepository.java
│   └── CourseEnrollmentRepository.java
├── quiz/
│   ├── QuizRepository.java
│   ├── QuestionRepository.java
│   ├── QuizAttemptRepository.java
│   └── QuizSessionRepository.java
├── analytics/
│   ├── QuestionAnalyticsRepository.java
│   └── UserAnalyticsRepository.java
├── security/
│   ├── AuditLogRepository.java
│   └── SecurityIncidentRepository.java
└── file/
    ├── FileRepository.java
    └── FileVariantRepository.java
```

## Detailed Repository Documentation

### 1. Authentication & User Management

#### UserRepository
**Purpose**: Core user management with advanced teacher functionality
**Key Features**:
- Basic user operations (email, username, role-based queries)
- Teacher-specific queries (specialization, verification, qualifications)
- User status and security (locked users, failed attempts, MFA)
- Activity tracking and engagement metrics
- Teacher portfolio and content management
- Bulk operations for user management
- Search and filtering capabilities
- Analytics support (user counts, registration stats)

**Example Methods**:
```java
Optional<User> findByEmail(String email);
Page<User> findActiveTeachers(@Param("role") UserRole role, Pageable pageable);
List<User> findVerifiedTeachers();
void updateLastActivity(@Param("userId") UUID userId, @Param("timestamp") LocalDateTime timestamp);
List<Object[]> countUsersByRole();
```

#### UserProfileRepository
**Purpose**: Extended user profile management with teacher portfolios
**Key Features**:
- Profile lookup and management
- Teacher portfolio queries (certifications, qualifications, experience)
- Performance statistics and analytics
- Notification and privacy settings
- Accessibility features
- Bulk profile updates
- Search and discovery capabilities

#### AuthTokenRepository
**Purpose**: Authentication token lifecycle management
**Key Features**:
- Token validation and lookup
- Token lifecycle management (creation, expiration, usage)
- Security and cleanup operations
- Token type management (email verification, password reset, MFA, API)
- Analytics and monitoring

### 2. Core Institutional Management

#### InstitutionRepository
**Purpose**: Multi-institutional support with comprehensive management
**Key Features**:
- Basic institution queries (slug, domain, type, status)
- Multi-institutional support (active institutions, subscription tiers)
- School district management (district coordination, resource sharing)
- University & higher education support (research, LMS integration)
- Corporate & training organization support
- Feature and limit management
- Search and filtering capabilities
- Analytics and reporting (user statistics, growth metrics)
- Compliance and monitoring

**Example Methods**:
```java
Optional<Institution> findBySlug(String slug);
List<Institution> findActiveInstitutions();
List<Institution> findSchoolDistricts();
List<Institution> findHigherEducationInstitutions();
List<Object[]> getInstitutionUserStatistics();
```

#### DepartmentRepository
**Purpose**: Hierarchical department management within institutions
**Key Features**:
- Basic department operations
- Hierarchical structure management (parent-child relationships)
- Department leadership (head assignments)
- Analytics and reporting
- Bulk operations

#### UserInstitutionRepository
**Purpose**: User-institution relationship management
**Key Features**:
- Membership management (roles, status, lifecycle)
- Role-based queries (students, teachers, admins)
- Department-based organization
- Student and employee ID management
- Analytics and reporting (retention, distribution)
- Permission and access control
- Bulk operations

#### CourseRepository
**Purpose**: Academic course management with comprehensive functionality
**Key Features**:
- Basic course operations (institution, department, instructor)
- Active course management
- Academic term management (semester, year, scheduling)
- Enrollment and capacity management
- Course level and prerequisites
- Instructor management and workload
- Search and filtering
- Quiz and assessment integration
- Analytics and reporting

#### CourseEnrollmentRepository
**Purpose**: Student course enrollment management
**Key Features**:
- Enrollment lifecycle management
- Enrollment type management (regular, audit, credit, guest)
- Status tracking (active, completed, dropped, withdrawn)
- Grading and performance tracking
- Student academic history
- Instructor course management
- Analytics and reporting
- Search and filtering

### 3. Quiz & Assessment System

#### QuizRepository
**Purpose**: Comprehensive quiz management with advanced features
**Key Features**:
- Basic quiz operations (creator, course, institution)
- Teacher quiz management (status, publishing, templates)
- Quiz discovery and access (public, join codes, guest access)
- Search and filtering capabilities
- Quiz templates and sharing
- Scheduling and availability
- Analytics support (popularity, performance)
- Collaboration and version control
- Student access and invitation management
- Advanced quiz features (adaptive, proctored, timed)

**Example Methods**:
```java
Page<Quiz> findByCreatorId(UUID creatorId, Pageable pageable);
List<Quiz> findAvailableQuizzes(@Param("now") LocalDateTime now);
Optional<Quiz> findByJoinCode(@Param("joinCode") String joinCode, @Param("now") LocalDateTime now);
void updateQuizStatus(@Param("quizId") UUID quizId, @Param("status") QuizStatus status);
```

#### QuestionRepository
**Purpose**: Question bank management with rich question types
**Key Features**:
- Teacher question bank management
- Search and filtering (text, type, subject, tags)
- Question bank organization (tags, subjects, similarity)
- Sharing and templates (public, shareable)
- Analytics and performance tracking
- Versioning support
- Multimedia and rich content support
- Accessibility and compliance
- Bulk operations
- Statistics and reporting

#### QuizAttemptRepository
**Purpose**: Quiz attempt management with comprehensive tracking
**Key Features**:
- Student attempt management (status, progress, limits)
- Teacher monitoring (all attempts, recent activity)
- Live quiz session support (participants, leaderboards)
- Analytics and performance tracking
- Time and progress monitoring
- Proctoring and security integration
- Email invitation and QR code tracking
- Bulk operations
- Reporting and statistics

#### QuizSessionRepository
**Purpose**: Live quiz session management with real-time features
**Key Features**:
- Session lifecycle management (scheduled, active, completed)
- Real-time features (leaderboards, live results)
- Participation management (capacity, late join, registration)
- Host management and analytics
- Session code management
- Time-based operations
- Search and filtering
- Analytics and reporting
- Bulk operations

### 4. Analytics & Reporting

#### QuestionAnalyticsRepository
**Purpose**: Detailed question performance analytics
**Key Features**:
- Performance analysis (success rates, difficulty, discrimination)
- Time-based analysis (response times, efficiency)
- Teacher-specific analytics (question bank performance)
- Institutional analytics (question overview, top performers)
- Quiz-specific analytics (question distribution)
- Trending and pattern analysis
- Bulk operations for data updates
- Comprehensive reporting queries

#### UserAnalyticsRepository
**Purpose**: Student performance analytics and learning insights
**Key Features**:
- Student performance analysis (high/low performers, trends)
- Course-specific analytics (top students, performance distribution)
- Institutional analytics (performance overview)
- Engagement and participation tracking
- Learning patterns and insights (question preferences, topics)
- Time-based analysis (efficiency, time usage)
- Teacher-specific queries (class performance)
- Bulk operations for data updates

### 5. Security & Audit

#### AuditLogRepository
**Purpose**: Comprehensive audit logging and security monitoring
**Key Features**:
- Basic audit queries (user, entity, action, severity)
- Time-based queries (ranges, recent activity)
- User activity tracking (login patterns, failed attempts)
- Entity change tracking (modification history, field changes)
- Security monitoring (high severity events, privilege changes)
- Compliance and reporting
- Analytics and statistics
- Search and filtering
- Data retention management
- Performance monitoring

#### SecurityIncidentRepository
**Purpose**: Security incident management and monitoring
**Key Features**:
- Incident lifecycle management (status, assignment, resolution)
- Severity and priority management
- Incident type analysis (cheating, tab switching, copy-paste)
- User pattern analysis (repeat offenders, critical incidents)
- Detection and confidence tracking
- Quiz and session analysis
- Bulk operations (assignment, resolution)
- Analytics and reporting
- Search and filtering

### 6. File Management

#### FileRepository
**Purpose**: Comprehensive file management with multimedia support
**Key Features**:
- Basic file operations (uploader, type, status, MIME type)
- File status management (processing, ready, failed, archived)
- Multimedia file queries (images, videos, audio, documents)
- Storage and size management (large files, storage usage)
- Access control (public/private, permissions)
- File processing (pending, completed, failed)
- Lifecycle management (expiration, cleanup, unused files)
- Search and filtering
- Analytics and reporting
- Deduplication support

#### FileVariantRepository
**Purpose**: File variant management (thumbnails, formats, sizes)
**Key Features**:
- Basic variant operations (parent file, variant name)
- Thumbnail management (different sizes)
- Format conversion variants (WebP, AVIF, compressed)
- Size-based queries (file size, dimensions)
- Optimization and processing
- Storage optimization
- Bulk operations
- Analytics and reporting
- Specialized queries (responsive images, modern formats)

## Key Technical Features

### 1. Advanced Query Capabilities
- **JSONB Support**: Extensive use of JSON functions for flexible data storage
- **Full-Text Search**: Search vectors for quiz and question content
- **Aggregation Queries**: Complex statistical and analytical queries
- **Window Functions**: Advanced analytics with ranking and partitioning
- **Recursive Queries**: Hierarchical data traversal (departments, prerequisites)

### 2. Performance Optimizations
- **Pagination Support**: All list queries support `Pageable` parameters
- **Lazy Loading**: Proper `@ManyToOne` and `@OneToOne` relationships
- **Bulk Operations**: `@Modifying` queries for efficient batch updates
- **Index-Friendly Queries**: Queries designed to use database indexes effectively

### 3. Security Features
- **Audit Trail**: Comprehensive logging of all data changes
- **Access Control**: Permission-based queries and data filtering
- **Data Retention**: Automated cleanup and archival capabilities
- **Incident Tracking**: Security event monitoring and response

### 4. Multi-Tenancy Support
- **Institution Isolation**: Data segregation by institution
- **Role-Based Access**: Different access levels within institutions
- **Department Structure**: Hierarchical organization support
- **Resource Sharing**: Controlled sharing between institutions

### 5. Real-Time Features
- **Live Sessions**: Support for real-time quiz sessions
- **Participation Tracking**: Real-time participant monitoring
- **Status Updates**: Dynamic status management
- **Event Streaming**: Support for real-time notifications

## Integration Points

### 1. Service Layer Integration
All repositories are designed to be consumed by service classes that implement business logic and transaction management.

### 2. Cache Integration
Repositories support caching strategies through Spring's caching abstraction, particularly for frequently accessed data like user profiles and quiz metadata.

### 3. Event Publishing
Repository operations can trigger domain events for real-time features and external system integration.

### 4. Metrics and Monitoring
All repositories include analytics queries that support dashboard and reporting requirements.

## Usage Examples

### Teacher Quiz Management
```java
// Find teacher's quizzes with status
Page<Quiz> teacherQuizzes = quizRepository.findByCreatorId(teacherId, pageable);

// Get quiz statistics
List<Object[]> stats = quizRepository.getTeacherQuizStatistics(teacherId);

// Update quiz status
quizRepository.updateQuizStatus(quizId, QuizStatus.PUBLISHED);
```

### Student Performance Analytics
```java
// Find high-performing students
List<UserAnalytics> topStudents = userAnalyticsRepository.findHighPerformingStudents(
    new BigDecimal("80"), 5, pageable);

// Get course performance distribution
Object[] distribution = userAnalyticsRepository.getCoursePerformanceDistribution(courseId);
```

### Institution Management
```java
// Find active institutions with statistics
List<Institution> institutions = institutionRepository.findActiveInstitutions();
List<Object[]> stats = institutionRepository.getInstitutionUserStatistics();

// Manage user-institution relationships
List<UserInstitution> members = userInstitutionRepository.findActiveInstitutionUsers(institutionId);
```

### File Management
```java
// Upload and process files
File uploadedFile = fileRepository.save(newFile);
fileRepository.updateProcessingStatus(fileId, "PROCESSING", metadata);

// Find multimedia content
List<File> images = fileRepository.findImageFiles();
List<FileVariant> thumbnails = fileVariantRepository.findThumbnailVariants();
```

## Conclusion

The repository layer provides a comprehensive, scalable, and feature-rich data access foundation for the SmartQuiz platform. It supports all requested functionality including:

- **Individual Teacher Functionality**: Complete quiz creation, student management, and analytics
- **Multi-Institutional Support**: School districts, universities, and corporate training
- **Advanced Question Types**: Rich multimedia and interactive assessments
- **Real-Time Features**: Live sessions, leaderboards, and instant feedback
- **University-Level Examinations**: Formal examination management and proctoring
- **Analytics & Reporting**: Comprehensive performance tracking and insights

The architecture is designed for scalability, maintainability, and extensibility, providing a solid foundation for building the complete SmartQuiz application.