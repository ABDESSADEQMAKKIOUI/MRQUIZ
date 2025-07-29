package com.example.mrquiz.mapper.quiz;

import com.example.mrquiz.dto.quiz.QuestionCreateDto;
import com.example.mrquiz.dto.quiz.QuestionResponseDto;
import com.example.mrquiz.dto.quiz.QuestionUpdateDto;
import com.example.mrquiz.entity.quiz.Question;
import com.example.mrquiz.mapper.BaseMapper;
import com.example.mrquiz.repository.auth.UserRepository;
import com.example.mrquiz.repository.core.CourseRepository;
import com.example.mrquiz.repository.core.InstitutionRepository;
import com.example.mrquiz.repository.file.FileRepository;
import com.example.mrquiz.repository.quiz.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper extends BaseMapper<Question, QuestionCreateDto, QuestionUpdateDto, QuestionResponseDto> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public QuestionMapper() {
        super(Question.class, QuestionCreateDto.class, QuestionUpdateDto.class, QuestionResponseDto.class);
    }

    @Override
    protected void afterCreateMapping(QuestionCreateDto createDto, Question entity) {
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

        // Set parent question if provided
        if (createDto.getParentQuestionId() != null) {
            questionRepository.findById(createDto.getParentQuestionId())
                    .ifPresent(entity::setParentQuestion);
        }

        // Set question files if provided
        if (createDto.getQuestionFiles() != null && !createDto.getQuestionFiles().isEmpty()) {
            List<com.example.mrquiz.entity.file.File> files = createDto.getQuestionFiles().stream()
                    .map(fileId -> fileRepository.findById(fileId).orElse(null))
                    .filter(file -> file != null)
                    .collect(Collectors.toList());
            entity.setQuestionFiles(files.stream().map(file -> file.getId()).toArray(java.util.UUID[]::new));
        }

        // Set default values
        if (entity.getPoints() == null) {
            entity.setPoints(java.math.BigDecimal.ONE);
        }
        if (entity.getNegativePoints() == null) {
            entity.setNegativePoints(java.math.BigDecimal.ZERO);
        }
        if (entity.getVersion() == null) {
            entity.setVersion(1);
        }
        if (entity.getUsageCount() == null) {
            entity.setUsageCount(0);
        }
    }

    @Override
    protected void afterUpdateMapping(QuestionUpdateDto updateDto, Question entity) {
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

        // Update parent question if provided
        if (shouldUpdate(updateDto.getParentQuestionId())) {
            if (updateDto.getParentQuestionId() != null) {
                questionRepository.findById(updateDto.getParentQuestionId())
                        .ifPresent(entity::setParentQuestion);
            } else {
                entity.setParentQuestion(null);
            }
        }

        // Update question files if provided
        if (shouldUpdate(updateDto.getQuestionFiles())) {
            if (updateDto.getQuestionFiles() != null && !updateDto.getQuestionFiles().isEmpty()) {
                List<com.example.mrquiz.entity.file.File> files = updateDto.getQuestionFiles().stream()
                        .map(fileId -> fileRepository.findById(fileId).orElse(null))
                        .filter(file -> file != null)
                        .collect(Collectors.toList());
                entity.setQuestionFiles(files.stream().map(file -> file.getId()).toArray(java.util.UUID[]::new));
            } else {
                entity.setQuestionFiles(new java.util.UUID[0]);
            }
        }
    }

    @Override
    protected void afterResponseMapping(Question entity, QuestionResponseDto responseDto) {
        // Convert UUID array back to List for response
        if (entity.getQuestionFiles() != null) {
            responseDto.setQuestionFiles(List.of(entity.getQuestionFiles()));
        }
    }

    /**
     * Create a summary response DTO for question bank listing
     */
    public QuestionResponseDto toSummaryResponseDto(Question entity) {
        if (entity == null) {
            return null;
        }

        QuestionResponseDto dto = new QuestionResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setQuestionText(entity.getQuestionText());
        dto.setQuestionType(entity.getQuestionType());
        dto.setPoints(entity.getPoints());
        dto.setDifficultyLevel(entity.getDifficultyLevel());
        dto.setSubjectAreas(entity.getSubjectAreas());
        dto.setTags(entity.getTags());
        dto.setUsageCount(entity.getUsageCount());
        dto.setStatus(entity.getStatus());
        dto.setCreatorId(entity.getCreator().getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    /**
     * Create a public response DTO without sensitive information
     */
    public QuestionResponseDto toPublicResponseDto(Question entity) {
        if (entity == null) {
            return null;
        }

        QuestionResponseDto dto = toSummaryResponseDto(entity);
        // Remove sensitive information for public access
        dto.setCorrectAnswers(null);
        dto.setAnswerValidation(null);
        dto.setSolutionMethod(null);
        dto.setHint(null);

        return dto;
    }
}