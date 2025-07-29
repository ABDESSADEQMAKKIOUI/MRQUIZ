package com.example.mrquiz.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Configure mapping strategy
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSourceNameTokenizer(NameTokenizers.CAMEL_CASE)
                .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE)
                .setAmbiguityIgnored(false)
                .setSkipNullEnabled(true);

        // Configure custom mappings
        configureCustomMappings(mapper);
        
        return mapper;
    }

    private void configureCustomMappings(ModelMapper mapper) {
        // Skip password mapping for security
        mapper.typeMap(com.example.mrquiz.entity.auth.User.class, 
                      com.example.mrquiz.dto.auth.UserResponseDto.class)
                .addMappings(mapping -> {
                    mapping.skip(com.example.mrquiz.dto.auth.UserResponseDto::setPasswordHash);
                    mapping.skip(com.example.mrquiz.dto.auth.UserResponseDto::setMfaSecret);
                });

        // Skip sensitive token data
        mapper.typeMap(com.example.mrquiz.entity.auth.AuthToken.class,
                      com.example.mrquiz.dto.auth.AuthTokenResponseDto.class)
                .addMappings(mapping -> {
                    mapping.skip(com.example.mrquiz.dto.auth.AuthTokenResponseDto::setTokenHash);
                });

        // Map nested relationships properly
        configureEntityRelationshipMappings(mapper);
    }

    private void configureEntityRelationshipMappings(ModelMapper mapper) {
        // User to UserResponse mapping with profile image handling
        mapper.typeMap(com.example.mrquiz.entity.auth.User.class,
                      com.example.mrquiz.dto.auth.UserResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getProfileImage() != null ? src.getProfileImage().getId() : null,
                               com.example.mrquiz.dto.auth.UserResponseDto::setProfileImageId);
                });

        // Quiz to QuizResponse mapping with creator and course handling
        mapper.typeMap(com.example.mrquiz.entity.quiz.Quiz.class,
                      com.example.mrquiz.dto.quiz.QuizResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getCreator().getId(),
                               com.example.mrquiz.dto.quiz.QuizResponseDto::setCreatorId);
                    mapping.map(src -> src.getCourse() != null ? src.getCourse().getId() : null,
                               com.example.mrquiz.dto.quiz.QuizResponseDto::setCourseId);
                    mapping.map(src -> src.getInstitution() != null ? src.getInstitution().getId() : null,
                               com.example.mrquiz.dto.quiz.QuizResponseDto::setInstitutionId);
                    mapping.map(src -> src.getBannerImage() != null ? src.getBannerImage().getId() : null,
                               com.example.mrquiz.dto.quiz.QuizResponseDto::setBannerImageId);
                });

        // Question to QuestionResponse mapping
        mapper.typeMap(com.example.mrquiz.entity.quiz.Question.class,
                      com.example.mrquiz.dto.quiz.QuestionResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getCreator().getId(),
                               com.example.mrquiz.dto.quiz.QuestionResponseDto::setCreatorId);
                    mapping.map(src -> src.getInstitution() != null ? src.getInstitution().getId() : null,
                               com.example.mrquiz.dto.quiz.QuestionResponseDto::setInstitutionId);
                    mapping.map(src -> src.getCourse() != null ? src.getCourse().getId() : null,
                               com.example.mrquiz.dto.quiz.QuestionResponseDto::setCourseId);
                    mapping.map(src -> src.getParentQuestion() != null ? src.getParentQuestion().getId() : null,
                               com.example.mrquiz.dto.quiz.QuestionResponseDto::setParentQuestionId);
                });

        // File to FileResponse mapping
        mapper.typeMap(com.example.mrquiz.entity.file.File.class,
                      com.example.mrquiz.dto.file.FileResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getUploadedBy().getId(),
                               com.example.mrquiz.dto.file.FileResponseDto::setUploadedById);
                });

        // Institution mappings
        mapper.typeMap(com.example.mrquiz.entity.core.Institution.class,
                      com.example.mrquiz.dto.core.InstitutionResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getLogo() != null ? src.getLogo().getId() : null,
                               com.example.mrquiz.dto.core.InstitutionResponseDto::setLogoId);
                });

        // Department mappings
        mapper.typeMap(com.example.mrquiz.entity.core.Department.class,
                      com.example.mrquiz.dto.core.DepartmentResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getInstitution().getId(),
                               com.example.mrquiz.dto.core.DepartmentResponseDto::setInstitutionId);
                    mapping.map(src -> src.getParentDepartment() != null ? src.getParentDepartment().getId() : null,
                               com.example.mrquiz.dto.core.DepartmentResponseDto::setParentDepartmentId);
                    mapping.map(src -> src.getHeadUser() != null ? src.getHeadUser().getId() : null,
                               com.example.mrquiz.dto.core.DepartmentResponseDto::setHeadUserId);
                });

        // Course mappings
        mapper.typeMap(com.example.mrquiz.entity.core.Course.class,
                      com.example.mrquiz.dto.core.CourseResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getInstitution().getId(),
                               com.example.mrquiz.dto.core.CourseResponseDto::setInstitutionId);
                    mapping.map(src -> src.getDepartment() != null ? src.getDepartment().getId() : null,
                               com.example.mrquiz.dto.core.CourseResponseDto::setDepartmentId);
                    mapping.map(src -> src.getInstructor() != null ? src.getInstructor().getId() : null,
                               com.example.mrquiz.dto.core.CourseResponseDto::setInstructorId);
                    mapping.map(src -> src.getCoverImage() != null ? src.getCoverImage().getId() : null,
                               com.example.mrquiz.dto.core.CourseResponseDto::setCoverImageId);
                });

        // QuizAttempt mappings
        mapper.typeMap(com.example.mrquiz.entity.quiz.QuizAttempt.class,
                      com.example.mrquiz.dto.quiz.QuizAttemptResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getQuiz().getId(),
                               com.example.mrquiz.dto.quiz.QuizAttemptResponseDto::setQuizId);
                    mapping.map(src -> src.getUser().getId(),
                               com.example.mrquiz.dto.quiz.QuizAttemptResponseDto::setUserId);
                    mapping.map(src -> src.getSession() != null ? src.getSession().getId() : null,
                               com.example.mrquiz.dto.quiz.QuizAttemptResponseDto::setSessionId);
                });

        // Analytics mappings
        configureAnalyticsMappings(mapper);
    }

    private void configureAnalyticsMappings(ModelMapper mapper) {
        // QuestionAnalytics mappings
        mapper.typeMap(com.example.mrquiz.entity.analytics.QuestionAnalytics.class,
                      com.example.mrquiz.dto.analytics.QuestionAnalyticsResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getQuestion().getId(),
                               com.example.mrquiz.dto.analytics.QuestionAnalyticsResponseDto::setQuestionId);
                    mapping.map(src -> src.getQuiz() != null ? src.getQuiz().getId() : null,
                               com.example.mrquiz.dto.analytics.QuestionAnalyticsResponseDto::setQuizId);
                    mapping.map(src -> src.getInstitution() != null ? src.getInstitution().getId() : null,
                               com.example.mrquiz.dto.analytics.QuestionAnalyticsResponseDto::setInstitutionId);
                });

        // UserAnalytics mappings
        mapper.typeMap(com.example.mrquiz.entity.analytics.UserAnalytics.class,
                      com.example.mrquiz.dto.analytics.UserAnalyticsResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getUser().getId(),
                               com.example.mrquiz.dto.analytics.UserAnalyticsResponseDto::setUserId);
                    mapping.map(src -> src.getCourse() != null ? src.getCourse().getId() : null,
                               com.example.mrquiz.dto.analytics.UserAnalyticsResponseDto::setCourseId);
                    mapping.map(src -> src.getInstitution() != null ? src.getInstitution().getId() : null,
                               com.example.mrquiz.dto.analytics.UserAnalyticsResponseDto::setInstitutionId);
                });

        // Security mappings
        mapper.typeMap(com.example.mrquiz.entity.security.SecurityIncident.class,
                      com.example.mrquiz.dto.security.SecurityIncidentResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getUser() != null ? src.getUser().getId() : null,
                               com.example.mrquiz.dto.security.SecurityIncidentResponseDto::setUserId);
                    mapping.map(src -> src.getAttempt() != null ? src.getAttempt().getId() : null,
                               com.example.mrquiz.dto.security.SecurityIncidentResponseDto::setAttemptId);
                    mapping.map(src -> src.getSession() != null ? src.getSession().getId() : null,
                               com.example.mrquiz.dto.security.SecurityIncidentResponseDto::setSessionId);
                    mapping.map(src -> src.getAssignedTo() != null ? src.getAssignedTo().getId() : null,
                               com.example.mrquiz.dto.security.SecurityIncidentResponseDto::setAssignedToId);
                    mapping.map(src -> src.getResolvedBy() != null ? src.getResolvedBy().getId() : null,
                               com.example.mrquiz.dto.security.SecurityIncidentResponseDto::setResolvedById);
                });
    }
}