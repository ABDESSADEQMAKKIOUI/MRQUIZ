# DTOs (Data Transfer Objects) Summary

This document provides a comprehensive overview of all DTOs created for the MrQuiz platform, organized by domain and functionality.

## Project Structure

```
src/main/java/com/example/mrquiz/dto/
├── auth/                    # Authentication & User Management DTOs
├── file/                    # File Management DTOs
├── core/                    # Core Business Logic DTOs
├── quiz/                    # Quiz & Assessment DTOs
├── analytics/               # Analytics & Reporting DTOs
└── security/                # Security & Audit DTOs
```

## 1. Authentication & User Management DTOs (`auth/`)

### User DTOs
- **`UserCreateDto`**: User registration with validation
  - Required: email, password, firstName, lastName, role
  - Optional: username, phone, profileImageId, timezone, language, metadata, preferences
  - Validation: Email format, password min 8 chars, size constraints

- **`UserUpdateDto`**: User profile updates
  - All fields optional for partial updates
  - Includes: role, status, phone verification, MFA settings

- **`UserResponseDto`**: User information response (no sensitive data)
  - Excludes: passwordHash, mfaSecret
  - Includes: all profile data, timestamps, status information

### User Profile DTOs
- **`UserProfileCreateDto`**: Extended profile creation
  - Required: userId
  - Includes: bio, social links, academic/professional info, certifications
  - Settings: notifications, privacy, accessibility

- **`UserProfileUpdateDto`**: Profile updates
  - All fields optional
  - Includes: quiz statistics (totalQuizzesTaken, averageScore)

- **`UserProfileResponseDto`**: Complete profile response
  - All profile data with timestamps

### Authentication Token DTOs
- **`AuthTokenCreateDto`**: Token creation
  - Required: userId, tokenType, expiresAt
  - Optional: metadata for token-specific data

- **`AuthTokenResponseDto`**: Token information (no hash)
  - Excludes: tokenHash for security
  - Includes: type, expiration, usage status

## 2. File Management DTOs (`file/`)

### File DTOs
- **`FileCreateDto`**: File upload information
  - Required: filename, originalFilename, filePath, fileSize, mimeType, fileType, fileHash, uploadedById
  - Optional: storage provider details, dimensions, duration, access permissions
  - Validation: Positive file size, size constraints

- **`FileUpdateDto`**: File metadata updates
  - Optional: status, public flag, storage details, processing status
  - Lifecycle: expiresAt, archivedAt

- **`FileResponseDto`**: Complete file information
  - All file metadata, storage details, processing status
  - Timestamps and lifecycle information

### File Variant DTOs
- **`FileVariantCreateDto`**: File variant creation (thumbnails, different formats)
  - Required: parentFileId, variantName, filePath, fileSize, mimeType
  - Optional: dimensions, processing metadata

- **`FileVariantResponseDto`**: Variant information
  - Complete variant details with timestamps

## 3. Core Business Logic DTOs (`core/`)

### Institution DTOs
- **`InstitutionCreateDto`**: Institution registration
  - Required: name, type
  - Optional: contact info, address, domain, branding, features, limits
  - Default: status=ACTIVE, subscriptionTier=basic

- **`InstitutionUpdateDto`**: Institution updates
  - All fields optional for partial updates
  - Includes: settings, branding, subscription management

- **`InstitutionResponseDto`**: Complete institution information
  - All institution data with configuration and timestamps

### Department DTOs
- **`DepartmentCreateDto`**: Department creation
  - Required: institutionId, name
  - Optional: parentDepartmentId (for hierarchical structure), code, description, headUserId

- **`DepartmentUpdateDto`**: Department updates
  - Support for hierarchy changes, head assignments

- **`DepartmentResponseDto`**: Department information
  - Complete department data with relationships

### Course DTOs
- **`CourseCreateDto`**: Academic course creation
  - Required: institutionId, code, name
  - Optional: department, academic details (credits, level, prerequisites)
  - Schedule: semester, academicYear, start/end dates
  - Staff: instructorId, teachingAssistants

- **`CourseUpdateDto`**: Course updates
  - All fields optional for flexible updates
  - Support for instructor changes, schedule updates

- **`CourseResponseDto`**: Complete course information
  - All course data, academic details, staff assignments

## 4. Quiz & Assessment DTOs (`quiz/`)

### Quiz DTOs
- **`QuizCreateDto`**: Quiz creation with comprehensive settings
  - Required: creatorId, title
  - Optional: courseId, institutionId for context
  - Configuration: type, scoring, timing, attempts, question display
  - Access control: availability windows, password, IP restrictions
  - Features: shuffle options, feedback settings, accessibility

- **`QuizUpdateDto`**: Quiz updates
  - All fields optional for partial updates
  - Support for status changes, configuration updates

- **`QuizResponseDto`**: Complete quiz information
  - All quiz settings, configuration, and metadata

### Question DTOs
- **`QuestionCreateDto`**: Question bank creation
  - Required: creatorId, questionText, questionType, questionData
  - Classification: difficulty, bloom taxonomy, learning objectives, tags
  - Content: explanation, hint, solution method, file attachments
  - Grading: points, negative points, answer validation
  - Accessibility: alt text, accessibility metadata

- **`QuestionUpdateDto`**: Question updates
  - Support for content updates, classification changes
  - Version control support

- **`QuestionResponseDto`**: Complete question information
  - All question content, metadata, usage statistics
  - Analytics: usage count, difficulty/discrimination indices

### Quiz Attempt DTOs
- **`QuizAttemptCreateDto`**: Attempt initiation
  - Required: quizId, userId
  - Optional: sessionId for live sessions
  - Technical: IP address, user agent, browser/device info
  - Security: security flags, proctoring data

- **`QuizAttemptUpdateDto`**: Attempt progress/completion
  - Status updates, scoring, timing
  - Security monitoring data

- **`QuizAttemptResponseDto`**: Complete attempt information
  - All attempt data, scores, timing, security information

## 5. Analytics & Reporting DTOs (`analytics/`)

### Question Analytics DTOs
- **`QuestionAnalyticsCreateDto`**: Question performance tracking
  - Required: questionId, periodStart, periodEnd
  - Metrics: attempt counts, success rates, difficulty indices
  - Timing: average/median time spent, efficiency scores
  - Analysis: answer distribution, common mistakes

- **`QuestionAnalyticsUpdateDto`**: Analytics updates
  - Support for recalculated metrics

- **`QuestionAnalyticsResponseDto`**: Complete analytics data
  - All performance metrics with calculation timestamp

### User Analytics DTOs
- **`UserAnalyticsCreateDto`**: Student performance tracking
  - Required: userId, periodStart, periodEnd
  - Context: courseId, institutionId, subjectArea
  - Performance: quiz statistics, score metrics, improvement trends
  - Engagement: participation rates, review frequency
  - Learning: preferred question types, strong/weak topics

- **`UserAnalyticsResponseDto`**: Complete user analytics
  - All performance and engagement metrics with timestamps

## 6. Security & Audit DTOs (`security/`)

### Audit Log DTOs
- **`AuditLogCreateDto`**: System activity logging
  - Required: entityType, action
  - Context: userId, sessionId, entityId
  - Changes: oldValues, newValues, changedFields
  - Request: IP address, user agent, request details
  - Classification: severity level

- **`AuditLogResponseDto`**: Complete audit information
  - All logged activity data with timestamps

### Security Incident DTOs
- **`SecurityIncidentCreateDto`**: Security event reporting
  - Required: incidentType, title
  - Context: userId, attemptId, sessionId
  - Detection: detectedBy, detectionMethod, confidenceScore
  - Evidence: evidence data, related log references
  - Management: status, assignment, actions taken

- **`SecurityIncidentUpdateDto`**: Incident management
  - Status updates, assignment changes
  - Resolution: resolvedBy, resolutionNotes, resolvedAt

- **`SecurityIncidentResponseDto`**: Complete incident information
  - All incident data, resolution status, timestamps

## Key Features & Design Patterns

### 1. Validation Annotations
- **`@NotNull`**, **`@NotBlank`**: Required field validation
- **`@Email`**: Email format validation
- **`@Size`**: String length constraints
- **`@Positive`**: Numeric value validation
- Custom messages for user-friendly error responses

### 2. Flexible Data Storage
- **`Map<String, Object>`**: JSON-like flexible data storage
- **`List<String>`**: Array fields for tags, topics, etc.
- **`List<UUID>`**: Reference arrays for relationships

### 3. Security Considerations
- **Response DTOs**: Exclude sensitive data (passwords, tokens)
- **Create DTOs**: Include only necessary fields for creation
- **Update DTOs**: All optional fields for partial updates

### 4. Timestamp Management
- **Create DTOs**: No timestamp fields (handled by entities)
- **Update DTOs**: Specific timestamps when needed (resolvedAt, etc.)
- **Response DTOs**: Include all relevant timestamps

### 5. UUID-Based Relationships
- All entity references use UUID for consistency
- Foreign key relationships clearly defined
- Support for optional relationships (nullable UUIDs)

### 6. Extensibility
- **Metadata fields**: Support for future feature additions
- **Settings objects**: Flexible configuration storage
- **JSONB mappings**: Complex data structures as needed

## Usage Examples

### Creating a User
```java
UserCreateDto userDto = new UserCreateDto();
userDto.setEmail("teacher@example.com");
userDto.setPassword("securePassword123");
userDto.setFirstName("John");
userDto.setLastName("Doe");
userDto.setRole(UserRole.TEACHER);
```

### Creating a Quiz
```java
QuizCreateDto quizDto = new QuizCreateDto();
quizDto.setCreatorId(teacherId);
quizDto.setTitle("Math Quiz 1");
quizDto.setQuizType(QuizType.QUIZ);
quizDto.setTimeLimit(60); // 60 minutes
quizDto.setAttemptsAllowed(3);
quizDto.setShowResultsImmediately(true);
```

### Creating a Question
```java
QuestionCreateDto questionDto = new QuestionCreateDto();
questionDto.setCreatorId(teacherId);
questionDto.setQuestionText("What is 2 + 2?");
questionDto.setQuestionType(QuestionType.MULTIPLE_CHOICE);
questionDto.setPoints(BigDecimal.valueOf(1.0));

Map<String, Object> questionData = new HashMap<>();
questionData.put("choices", Arrays.asList(
    Map.of("text", "3", "isCorrect", false),
    Map.of("text", "4", "isCorrect", true),
    Map.of("text", "5", "isCorrect", false)
));
questionDto.setQuestionData(questionData);
```

## Integration Points

### 1. Controller Layer
- DTOs used for request/response bodies
- Validation annotations processed by Spring
- Automatic JSON serialization/deserialization

### 2. Service Layer
- DTOs converted to/from entities
- Business logic validation
- Data transformation and mapping

### 3. Repository Layer
- Entities used for database operations
- DTOs not directly used at this layer

## Best Practices Implemented

1. **Separation of Concerns**: Different DTOs for different operations
2. **Validation**: Comprehensive input validation
3. **Security**: Sensitive data excluded from responses
4. **Flexibility**: Optional fields for partial updates
5. **Consistency**: Uniform naming and structure patterns
6. **Documentation**: Clear field purposes and constraints
7. **Extensibility**: Support for future feature additions

This DTO structure provides a robust foundation for the MrQuiz platform, supporting all major functionalities while maintaining security, flexibility, and extensibility.