# SmartQuiz Platform - JPA Entities Summary

This document provides an overview of all the JPA entities created for the SmartQuiz platform, organized by functional domains.

## Project Structure

```
src/main/java/com/example/mrquiz/
├── entity/
│   ├── BaseEntity.java                    # Base entity with common fields
│   ├── auth/                              # Authentication & User Management
│   │   ├── User.java
│   │   ├── UserProfile.java
│   │   ├── AuthToken.java
│   │   └── UserSession.java
│   ├── core/                              # Core Business Entities
│   │   ├── Institution.java
│   │   ├── Department.java
│   │   ├── UserInstitution.java
│   │   ├── Course.java
│   │   └── CourseEnrollment.java
│   ├── quiz/                              # Quiz System
│   │   ├── Quiz.java
│   │   ├── Question.java
│   │   ├── QuizQuestion.java
│   │   ├── QuizSession.java
│   │   ├── QuizAttempt.java
│   │   ├── QuestionResponse.java
│   │   └── ResponseFile.java
│   ├── file/                              # File Management
│   │   ├── File.java
│   │   └── FileVariant.java
│   ├── analytics/                         # Analytics & Reporting
│   │   ├── QuestionAnalytics.java
│   │   └── UserAnalytics.java
│   └── security/                          # Security & Audit
│       ├── AuditLog.java
│       └── SecurityIncident.java
└── enums/                                 # Enum Types
    ├── UserRole.java
    ├── UserStatus.java
    ├── TokenType.java
    ├── InstitutionType.java
    ├── InstitutionStatus.java
    ├── InstitutionRole.java
    ├── MembershipStatus.java
    ├── CourseStatus.java
    ├── EnrollmentType.java
    ├── EnrollmentStatus.java
    ├── QuizType.java
    ├── QuizStatus.java
    ├── QuestionType.java
    ├── QuestionStatus.java
    ├── DifficultyLevel.java
    ├── SessionStatus.java
    ├── AttemptStatus.java
    ├── FileType.java
    ├── FileStatus.java
    ├── SeverityLevel.java
    └── IncidentType.java
```

## Entity Domains

### 1. Authentication & User Management (`auth` package)

#### User
- **Purpose**: Core user entity with authentication and profile information
- **Key Features**:
  - Email/username authentication
  - MFA support
  - Role-based access control
  - Security tracking (failed attempts, account locking)
  - Flexible metadata storage (JSONB)

#### UserProfile
- **Purpose**: Extended user profile information
- **Key Features**:
  - Academic and professional information
  - Social media links
  - Notification and privacy preferences
  - Performance statistics

#### AuthToken
- **Purpose**: Authentication tokens management
- **Key Features**:
  - Multiple token types (email verification, password reset, etc.)
  - Expiration and usage tracking
  - Secure token hashing

#### UserSession
- **Purpose**: User session tracking
- **Key Features**:
  - Session token management
  - Device and location tracking
  - Activity monitoring

### 2. Core Business Entities (`core` package)

#### Institution
- **Purpose**: Educational institutions/organizations
- **Key Features**:
  - Multi-tenant support
  - Branding and configuration
  - Subscription and billing integration
  - Feature flags and limits

#### Department
- **Purpose**: Organizational departments within institutions
- **Key Features**:
  - Hierarchical structure support
  - Department head assignment
  - Custom settings

#### UserInstitution
- **Purpose**: User-institution relationship mapping
- **Key Features**:
  - Role-based membership
  - Student/employee ID tracking
  - Permission management
  - Membership lifecycle tracking

#### Course
- **Purpose**: Academic courses
- **Key Features**:
  - Semester and academic year tracking
  - Instructor and TA assignment
  - Prerequisites management
  - Grading scale configuration

#### CourseEnrollment
- **Purpose**: Student course enrollments
- **Key Features**:
  - Multiple enrollment types (regular, audit, etc.)
  - Grade tracking
  - Enrollment lifecycle management

### 3. Quiz System (`quiz` package)

#### Quiz
- **Purpose**: Main quiz/assessment entity
- **Key Features**:
  - Multiple quiz types (quiz, exam, survey, etc.)
  - Advanced timing controls
  - Security and proctoring settings
  - Flexible question presentation options
  - Access control and scheduling

#### Question
- **Purpose**: Question bank with rich question types
- **Key Features**:
  - 17 different question types (including clinical cases)
  - Flexible JSON-based question data storage
  - File attachment support
  - Analytics and performance tracking
  - Versioning and sharing capabilities

#### QuizQuestion
- **Purpose**: Quiz-question relationship with customization
- **Key Features**:
  - Question ordering and grouping
  - Point override capabilities
  - Section organization
  - Per-question settings

#### QuizSession
- **Purpose**: Live quiz sessions for group activities
- **Key Features**:
  - Session code generation
  - Real-time participation
  - Leaderboard support
  - Access control

#### QuizAttempt
- **Purpose**: Individual quiz attempts
- **Key Features**:
  - Multiple attempt support
  - Comprehensive timing tracking
  - Security and proctoring data
  - Device and browser information
  - Scoring and grading

#### QuestionResponse
- **Purpose**: Individual question responses
- **Key Features**:
  - Flexible JSON-based answer storage
  - Automatic and manual grading support
  - Response timing and behavior tracking
  - Confidence level recording

#### ResponseFile
- **Purpose**: File attachments for question responses
- **Key Features**:
  - File upload support for responses
  - Description and ordering
  - Integration with file management system

### 4. File Management (`file` package)

#### File
- **Purpose**: Comprehensive file storage system
- **Key Features**:
  - Multiple storage providers (local, S3, etc.)
  - File processing and variants
  - Access control and permissions
  - Deduplication via hash
  - Lifecycle management

#### FileVariant
- **Purpose**: Different file formats and sizes
- **Key Features**:
  - Thumbnail generation
  - Format conversion
  - Size optimization
  - Processing metadata

### 5. Analytics & Reporting (`analytics` package)

#### QuestionAnalytics
- **Purpose**: Question performance analytics
- **Key Features**:
  - Success rate calculation
  - Difficulty and discrimination indices
  - Time analysis
  - Answer pattern analysis
  - Common mistake tracking

#### UserAnalytics
- **Purpose**: User performance analytics
- **Key Features**:
  - Comprehensive performance metrics
  - Learning pattern analysis
  - Engagement tracking
  - Improvement trends
  - Subject area analysis

### 6. Security & Audit (`security` package)

#### AuditLog
- **Purpose**: Comprehensive audit logging
- **Key Features**:
  - Change tracking (old/new values)
  - Request context capture
  - Action classification
  - Severity levels
  - User and session tracking

#### SecurityIncident
- **Purpose**: Security incident management
- **Key Features**:
  - Multiple incident types
  - Evidence collection
  - Investigation workflow
  - Resolution tracking
  - Confidence scoring

## Key Technical Features

### 1. PostgreSQL Integration
- **JSONB Support**: Extensive use of JSONB columns for flexible data storage
- **UUID Primary Keys**: All entities use UUID for better scalability
- **Comprehensive Indexing**: Strategic indexes for query performance
- **Enum Integration**: Java enums mapped to PostgreSQL custom types

### 2. JPA/Hibernate Features
- **Lazy Loading**: Optimized relationship loading
- **Auditing**: Automatic timestamp management
- **Custom Types**: JSONB type handling
- **Inheritance**: BaseEntity for common fields

### 3. Lombok Integration
- **@Data**: Automatic getters, setters, toString, equals, hashCode
- **@EqualsAndHashCode**: Proper equality handling with inheritance
- **Clean Code**: Reduced boilerplate

### 4. Flexible Architecture
- **JSON Storage**: Extensible metadata and settings
- **Multi-tenancy**: Institution-based data isolation
- **Versioning**: Question versioning support
- **File System**: Comprehensive file management

## Database Schema Compliance

All entities are designed to match the PostgreSQL database schema provided, including:
- Exact table and column names
- Proper foreign key relationships
- Index definitions
- Constraint specifications
- Data types and precision

## Usage Examples

### Creating a Quiz
```java
Quiz quiz = new Quiz();
quiz.setTitle("Midterm Exam");
quiz.setQuizType(QuizType.EXAM);
quiz.setCreator(user);
quiz.setCourse(course);
quiz.setTimeLimit(120); // 2 hours
quiz.setStatus(QuizStatus.DRAFT);
```

### Adding Questions to Quiz
```java
QuizQuestion quizQuestion = new QuizQuestion();
quizQuestion.setQuiz(quiz);
quizQuestion.setQuestion(question);
quizQuestion.setOrderIndex(1);
quizQuestion.setPoints(BigDecimal.valueOf(10));
```

### Recording Quiz Attempt
```java
QuizAttempt attempt = new QuizAttempt();
attempt.setQuiz(quiz);
attempt.setUser(student);
attempt.setStatus(AttemptStatus.IN_PROGRESS);
attempt.setStartedAt(LocalDateTime.now());
```

This entity structure provides a solid foundation for the SmartQuiz platform, supporting all the features outlined in the database schema while maintaining clean, maintainable Java code.