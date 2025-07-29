# ModelMapper Configuration Guide

This document provides a comprehensive guide to the ModelMapper configuration and usage in the MrQuiz platform.

## Overview

ModelMapper is configured to handle automatic mapping between DTOs and entities, with custom configurations for complex relationships and security considerations.

## Project Structure

```
src/main/java/com/example/mrquiz/
├── config/
│   ├── ModelMapperConfig.java      # Main ModelMapper configuration
│   └── SecurityConfig.java         # Security configuration (PasswordEncoder)
├── mapper/
│   ├── BaseMapper.java             # Abstract base mapper class
│   ├── auth/
│   │   └── UserMapper.java         # User-specific mapping logic
│   └── quiz/
│       ├── QuizMapper.java         # Quiz-specific mapping logic
│       └── QuestionMapper.java     # Question-specific mapping logic
└── service/
    └── MappingService.java         # Centralized mapping service
```

## Configuration Details

### 1. ModelMapperConfig.java

**Core Configuration:**
```java
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setSourceNameTokenizer(NameTokenizers.CAMEL_CASE)
                .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE)
                .setAmbiguityIgnored(false)
                .setSkipNullEnabled(true);
    }
}
```

**Key Settings:**
- **STRICT Matching**: Prevents ambiguous mappings
- **Field Matching**: Enables direct field access
- **Private Access**: Allows mapping of private fields
- **Skip Null**: Null values don't overwrite existing values
- **Camel Case**: Consistent naming convention

### 2. Security Mappings

**Password Security:**
```java
// Skip sensitive fields in response mappings
mapper.typeMap(User.class, UserResponseDto.class)
        .addMappings(mapping -> {
            mapping.skip(UserResponseDto::setPasswordHash);
            mapping.skip(UserResponseDto::setMfaSecret);
        });
```

**Token Security:**
```java
// Skip token hash in responses
mapper.typeMap(AuthToken.class, AuthTokenResponseDto.class)
        .addMappings(mapping -> {
            mapping.skip(AuthTokenResponseDto::setTokenHash);
        });
```

### 3. Relationship Mappings

**Entity to DTO ID Mapping:**
```java
// Map entity relationships to UUID fields
mapper.typeMap(Quiz.class, QuizResponseDto.class)
        .addMappings(mapping -> {
            mapping.map(src -> src.getCreator().getId(), QuizResponseDto::setCreatorId);
            mapping.map(src -> src.getCourse() != null ? src.getCourse().getId() : null, 
                       QuizResponseDto::setCourseId);
        });
```

## Mapper Classes

### 1. BaseMapper<E, CD, UD, RD>

**Generic Type Parameters:**
- `E`: Entity class
- `CD`: Create DTO class
- `UD`: Update DTO class
- `RD`: Response DTO class

**Core Methods:**
```java
public E toEntity(CD createDto)                    // Create new entity
public void updateEntity(UD updateDto, E entity)  // Update existing entity
public RD toResponseDto(E entity)                 // Entity to response DTO
public List<RD> toResponseDtoList(List<E> entities) // List mapping
public Page<RD> toResponseDtoPage(Page<E> entityPage) // Page mapping
```

**Hook Methods:**
```java
protected void afterCreateMapping(CD createDto, E entity)
protected void afterUpdateMapping(UD updateDto, E entity)
protected void afterResponseMapping(E entity, RD responseDto)
```

### 2. UserMapper

**Special Features:**
- Password hashing during creation
- Profile image relationship handling
- Public response DTO for privacy
- Password update method

**Usage Example:**
```java
@Autowired
private UserMapper userMapper;

// Create user
UserCreateDto createDto = new UserCreateDto();
createDto.setEmail("user@example.com");
createDto.setPassword("password123");
User user = userMapper.toEntity(createDto); // Password automatically hashed

// Update user
UserUpdateDto updateDto = new UserUpdateDto();
updateDto.setFirstName("John");
userMapper.updateEntity(updateDto, user);

// Get response
UserResponseDto response = userMapper.toResponseDto(user); // No sensitive data
```

### 3. QuizMapper

**Features:**
- Creator, course, institution relationship handling
- Banner image mapping
- Default value setting
- Summary response DTO

**Usage Example:**
```java
@Autowired
private QuizMapper quizMapper;

// Create quiz
QuizCreateDto createDto = new QuizCreateDto();
createDto.setCreatorId(teacherId);
createDto.setTitle("Math Quiz");
Quiz quiz = quizMapper.toEntity(createDto);

// Get summary (minimal data)
QuizResponseDto summary = quizMapper.toSummaryResponseDto(quiz);
```

### 4. QuestionMapper

**Features:**
- File attachment handling
- Parent question relationships
- Version control support
- Public response (no answers)

**Usage Example:**
```java
@Autowired
private QuestionMapper questionMapper;

// Create question with files
QuestionCreateDto createDto = new QuestionCreateDto();
createDto.setQuestionFiles(Arrays.asList(fileId1, fileId2));
Question question = questionMapper.toEntity(createDto);

// Get public version (no correct answers)
QuestionResponseDto publicDto = questionMapper.toPublicResponseDto(question);
```

## MappingService

**Centralized Access:**
```java
@Autowired
private MappingService mappingService;

// Access domain mappers
UserMapper userMapper = mappingService.getUserMapper();
QuizMapper quizMapper = mappingService.getQuizMapper();

// Generic mapping
SomeDto dto = mappingService.map(entity, SomeDto.class);
List<SomeDto> dtos = mappingService.mapList(entities, SomeDto.class);
Page<SomeDto> page = mappingService.mapPage(entityPage, SomeDto.class);

// Utility methods
SomeDto copy = mappingService.deepCopy(originalDto);
mappingService.mapToExisting(updateDto, existingEntity);
```

## Usage Patterns

### 1. Controller Layer

```java
@RestController
public class UserController {
    
    @Autowired
    private MappingService mappingService;
    
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateDto createDto) {
        User user = mappingService.getUserMapper().toEntity(createDto);
        user = userService.save(user);
        UserResponseDto response = mappingService.getUserMapper().toResponseDto(user);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id, 
                                                     @RequestBody UserUpdateDto updateDto) {
        User user = userService.findById(id);
        mappingService.getUserMapper().updateEntity(updateDto, user);
        user = userService.save(user);
        UserResponseDto response = mappingService.getUserMapper().toResponseDto(user);
        return ResponseEntity.ok(response);
    }
}
```

### 2. Service Layer

```java
@Service
public class QuizService {
    
    @Autowired
    private MappingService mappingService;
    
    public QuizResponseDto createQuiz(QuizCreateDto createDto) {
        Quiz quiz = mappingService.getQuizMapper().toEntity(createDto);
        quiz = quizRepository.save(quiz);
        return mappingService.getQuizMapper().toResponseDto(quiz);
    }
    
    public Page<QuizResponseDto> getQuizzes(Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findAll(pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }
}
```

## Advanced Features

### 1. Custom Converters

```java
// In ModelMapperConfig.java
private void configureCustomConverters(ModelMapper mapper) {
    // Date format converter
    Converter<String, LocalDateTime> stringToDateTime = context -> 
        LocalDateTime.parse(context.getSource(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    
    mapper.addConverter(stringToDateTime);
}
```

### 2. Conditional Mapping

```java
// In custom mapper
@Override
protected void afterCreateMapping(CreateDto createDto, Entity entity) {
    if (createDto.getSpecialField() != null) {
        // Custom logic for special cases
        entity.setProcessedField(processSpecialField(createDto.getSpecialField()));
    }
}
```

### 3. Validation Integration

```java
// In BaseMapper
public E toEntity(CD createDto) {
    validateDto(createDto); // Custom validation
    E entity = modelMapper.map(createDto, entityClass);
    afterCreateMapping(createDto, entity);
    return entity;
}
```

## Best Practices

### 1. Security
- Always skip sensitive fields in response mappings
- Use separate mappers for public vs private data
- Validate input DTOs before mapping

### 2. Performance
- Use lazy loading for relationships
- Consider caching for frequently mapped objects
- Use summary DTOs for list operations

### 3. Maintainability
- Keep mapping logic in mapper classes
- Use hook methods for custom logic
- Document complex mapping rules

### 4. Testing
- Test mapping configurations
- Validate security exclusions
- Test edge cases (null values, empty collections)

## Troubleshooting

### 1. Common Issues

**Ambiguous Mapping:**
```
Solution: Use STRICT matching strategy or explicit mappings
```

**Null Pointer Exceptions:**
```
Solution: Check null safety in custom mapping logic
```

**Circular References:**
```
Solution: Use @JsonIgnore or separate DTOs for relationships
```

### 2. Debugging

```java
// Get mapping information
String info = mappingService.getMappingInfo();
System.out.println(info);

// Validate mappings
mappingService.validateMappings();

// Check if mapping is possible
boolean canMap = mappingService.canMap(SourceClass.class, TargetClass.class);
```

## Migration Guide

### From Manual Mapping
1. Replace manual field assignments with mapper calls
2. Move custom logic to hook methods
3. Use centralized MappingService

### Adding New Entities
1. Create DTOs (Create, Update, Response)
2. Extend BaseMapper for the entity
3. Add to MappingService
4. Configure relationships in ModelMapperConfig

## Performance Considerations

### 1. Optimization Tips
- Use field matching for better performance
- Avoid deep object graphs in DTOs
- Cache mapper instances when possible
- Use batch operations for collections

### 2. Memory Usage
- Clear unnecessary references after mapping
- Use summary DTOs for large datasets
- Consider pagination for large collections

### 3. Monitoring
- Monitor mapping performance in production
- Log mapping errors and exceptions
- Track memory usage patterns

This configuration provides a robust, secure, and maintainable mapping solution for the MrQuiz platform, supporting all the complex relationships and security requirements while maintaining good performance and developer experience.