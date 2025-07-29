package com.example.mrquiz.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class BaseMapper<E, CD, UD, RD> {

    @Autowired
    protected ModelMapper modelMapper;

    protected final Class<E> entityClass;
    protected final Class<CD> createDtoClass;
    protected final Class<UD> updateDtoClass;
    protected final Class<RD> responseDtoClass;

    protected BaseMapper(Class<E> entityClass, Class<CD> createDtoClass, 
                        Class<UD> updateDtoClass, Class<RD> responseDtoClass) {
        this.entityClass = entityClass;
        this.createDtoClass = createDtoClass;
        this.updateDtoClass = updateDtoClass;
        this.responseDtoClass = responseDtoClass;
    }

    /**
     * Maps CreateDto to Entity for new entity creation
     */
    public E toEntity(CD createDto) {
        if (createDto == null) {
            return null;
        }
        E entity = modelMapper.map(createDto, entityClass);
        afterCreateMapping(createDto, entity);
        return entity;
    }

    /**
     * Maps UpdateDto to existing Entity for updates
     */
    public void updateEntity(UD updateDto, E entity) {
        if (updateDto == null || entity == null) {
            return;
        }
        modelMapper.map(updateDto, entity);
        afterUpdateMapping(updateDto, entity);
    }

    /**
     * Maps Entity to ResponseDto
     */
    public RD toResponseDto(E entity) {
        if (entity == null) {
            return null;
        }
        RD responseDto = modelMapper.map(entity, responseDtoClass);
        afterResponseMapping(entity, responseDto);
        return responseDto;
    }

    /**
     * Maps List of Entities to List of ResponseDtos
     */
    public List<RD> toResponseDtoList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps Page of Entities to Page of ResponseDtos
     */
    public Page<RD> toResponseDtoPage(Page<E> entityPage) {
        if (entityPage == null) {
            return null;
        }
        List<RD> responseDtos = toResponseDtoList(entityPage.getContent());
        return new PageImpl<>(responseDtos, entityPage.getPageable(), entityPage.getTotalElements());
    }

    /**
     * Hook method called after mapping CreateDto to Entity
     * Override in subclasses for custom post-mapping logic
     */
    protected void afterCreateMapping(CD createDto, E entity) {
        // Default implementation does nothing
    }

    /**
     * Hook method called after mapping UpdateDto to Entity
     * Override in subclasses for custom post-mapping logic
     */
    protected void afterUpdateMapping(UD updateDto, E entity) {
        // Default implementation does nothing
    }

    /**
     * Hook method called after mapping Entity to ResponseDto
     * Override in subclasses for custom post-mapping logic
     */
    protected void afterResponseMapping(E entity, RD responseDto) {
        // Default implementation does nothing
    }

    /**
     * Utility method to check if a field should be updated (not null)
     */
    protected boolean shouldUpdate(Object value) {
        return value != null;
    }

    /**
     * Utility method for conditional mapping
     */
    protected <T> T mapIfNotNull(Object source, Class<T> targetClass) {
        return source != null ? modelMapper.map(source, targetClass) : null;
    }
}