package com.example.mrquiz.mapper.quiz;

import com.example.mrquiz.dto.quiz.QuizCreateDto;
import com.example.mrquiz.dto.quiz.QuizResponseDto;
import com.example.mrquiz.dto.quiz.QuizUpdateDto;
import com.example.mrquiz.entity.quiz.Quiz;
import com.example.mrquiz.mapper.BaseMapper;
import com.example.mrquiz.repository.auth.UserRepository;
import com.example.mrquiz.repository.core.CourseRepository;
import com.example.mrquiz.repository.core.InstitutionRepository;
import com.example.mrquiz.repository.file.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper extends BaseMapper<Quiz, QuizCreateDto, QuizUpdateDto, QuizResponseDto> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private FileRepository fileRepository;

    public QuizMapper() {
        super(Quiz.class, QuizCreateDto.class, QuizUpdateDto.class, QuizResponseDto.class);
    }

    @Override
    protected void afterCreateMapping(QuizCreateDto createDto, Quiz entity) {
        // Set creator
        if (createDto.getCreatorId() != null) {
            userRepository.findById(createDto.getCreatorId())
                    .ifPresent(entity::setCreator);
        }

        // Set course if provided
        if (createDto.getCourseId() != null) {
            courseRepository.findById(createDto.getCourseId())
                    .ifPresent(entity::setCourse);
        }

        // Set institution if provided
        if (createDto.getInstitutionId() != null) {
            institutionRepository.findById(createDto.getInstitutionId())
                    .ifPresent(entity::setInstitution);
        }

        // Set banner image if provided
        if (createDto.getBannerImageId() != null) {
            fileRepository.findById(createDto.getBannerImageId())
                    .ifPresent(entity::setBannerImage);
        }

        // Set default values
        if (entity.getGradingMethod() == null) {
            entity.setGradingMethod("automatic");
        }
        if (entity.getAttemptsAllowed() == null) {
            entity.setAttemptsAllowed(1);
        }
        if (entity.getAttemptScoring() == null) {
            entity.setAttemptScoring("last");
        }
        if (entity.getQuestionsPerPage() == null) {
            entity.setQuestionsPerPage(1);
        }
    }

    @Override
    protected void afterUpdateMapping(QuizUpdateDto updateDto, Quiz entity) {
        // Update course if provided
        if (shouldUpdate(updateDto.getCourseId())) {
            if (updateDto.getCourseId() != null) {
                courseRepository.findById(updateDto.getCourseId())
                        .ifPresent(entity::setCourse);
            } else {
                entity.setCourse(null);
            }
        }

        // Update institution if provided
        if (shouldUpdate(updateDto.getInstitutionId())) {
            if (updateDto.getInstitutionId() != null) {
                institutionRepository.findById(updateDto.getInstitutionId())
                        .ifPresent(entity::setInstitution);
            } else {
                entity.setInstitution(null);
            }
        }

        // Update banner image if provided
        if (shouldUpdate(updateDto.getBannerImageId())) {
            if (updateDto.getBannerImageId() != null) {
                fileRepository.findById(updateDto.getBannerImageId())
                        .ifPresent(entity::setBannerImage);
            } else {
                entity.setBannerImage(null);
            }
        }
    }

    @Override
    protected void afterResponseMapping(Quiz entity, QuizResponseDto responseDto) {
        // Additional custom logic can be added here if needed
        // The ModelMapper configuration already handles the basic mappings
    }

    /**
     * Create a summary response DTO with minimal information
     */
    public QuizResponseDto toSummaryResponseDto(Quiz entity) {
        if (entity == null) {
            return null;
        }

        QuizResponseDto dto = new QuizResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setQuizType(entity.getQuizType());
        dto.setTotalPoints(entity.getTotalPoints());
        dto.setTimeLimit(entity.getTimeLimit());
        dto.setAttemptsAllowed(entity.getAttemptsAllowed());
        dto.setStatus(entity.getStatus());
        dto.setCreatorId(entity.getCreator().getId());
        dto.setCourseId(entity.getCourse() != null ? entity.getCourse().getId() : null);
        dto.setInstitutionId(entity.getInstitution() != null ? entity.getInstitution().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}