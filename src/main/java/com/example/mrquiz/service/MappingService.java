package com.example.mrquiz.service;

import com.example.mrquiz.mapper.auth.UserMapper;
import com.example.mrquiz.mapper.quiz.QuestionMapper;
import com.example.mrquiz.mapper.quiz.QuizMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized mapping service that provides access to all domain mappers
 * and common mapping operations across the application.
 */
@Service
public class MappingService {

    @Autowired
    private ModelMapper modelMapper;

    // Domain-specific mappers
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuizMapper quizMapper;

    @Autowired
    private QuestionMapper questionMapper;

    // Getters for domain mappers
    public UserMapper getUserMapper() {
        return userMapper;
    }

    public QuizMapper getQuizMapper() {
        return quizMapper;
    }

    public QuestionMapper getQuestionMapper() {
        return questionMapper;
    }

    /**
     * Generic mapping method for simple mappings
     */
    public <S, T> T map(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, targetClass);
    }

    /**
     * Generic mapping method for lists
     */
    public <S, T> List<T> mapList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }
        return sourceList.stream()
                .map(source -> map(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * Generic mapping method for pages
     */
    public <S, T> Page<T> mapPage(Page<S> sourcePage, Class<T> targetClass) {
        if (sourcePage == null) {
            return null;
        }
        List<T> mappedContent = mapList(sourcePage.getContent(), targetClass);
        return new PageImpl<>(mappedContent, sourcePage.getPageable(), sourcePage.getTotalElements());
    }

    /**
     * Map source object to existing target object (partial update)
     */
    public <S, T> void mapToExisting(S source, T target) {
        if (source != null && target != null) {
            modelMapper.map(source, target);
        }
    }

    /**
     * Conditional mapping - only map if source is not null
     */
    public <S, T> T mapIfNotNull(S source, Class<T> targetClass) {
        return source != null ? map(source, targetClass) : null;
    }

    /**
     * Map with custom configuration
     */
    public <S, T> T mapWithConfig(S source, Class<T> targetClass, 
                                  org.modelmapper.Converter<S, T> converter) {
        if (source == null) {
            return null;
        }
        
        ModelMapper customMapper = new ModelMapper();
        customMapper.addConverter(converter);
        return customMapper.map(source, targetClass);
    }

    /**
     * Deep copy an object (clone)
     */
    @SuppressWarnings("unchecked")
    public <T> T deepCopy(T source) {
        if (source == null) {
            return null;
        }
        return (T) modelMapper.map(source, source.getClass());
    }

    /**
     * Merge two objects of the same type
     */
    public <T> T merge(T source, T target) {
        if (source == null) {
            return target;
        }
        if (target == null) {
            return deepCopy(source);
        }
        
        modelMapper.map(source, target);
        return target;
    }

    /**
     * Check if two objects are mappable
     */
    public <S, T> boolean canMap(Class<S> sourceClass, Class<T> targetClass) {
        try {
            return modelMapper.getTypeMap(sourceClass, targetClass) != null ||
                   modelMapper.createTypeMap(sourceClass, targetClass) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get mapping statistics for debugging
     */
    public String getMappingInfo() {
        StringBuilder info = new StringBuilder();
        info.append("ModelMapper Configuration:\n");
        info.append("- Matching Strategy: ").append(modelMapper.getConfiguration().getMatchingStrategy()).append("\n");
        info.append("- Field Matching: ").append(modelMapper.getConfiguration().isFieldMatchingEnabled()).append("\n");
        info.append("- Skip Null: ").append(modelMapper.getConfiguration().isSkipNullEnabled()).append("\n");
        info.append("- Type Maps: ").append(modelMapper.getTypeMaps().size()).append("\n");
        
        return info.toString();
    }

    /**
     * Validate mapping configuration
     */
    public void validateMappings() {
        try {
            modelMapper.validate();
        } catch (Exception e) {
            throw new RuntimeException("ModelMapper validation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Reset ModelMapper to default configuration
     */
    public void resetConfiguration() {
        // This would require recreating the ModelMapper bean
        // Implementation depends on specific requirements
    }
}