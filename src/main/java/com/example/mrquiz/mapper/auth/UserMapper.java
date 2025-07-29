package com.example.mrquiz.mapper.auth;

import com.example.mrquiz.dto.auth.UserCreateDto;
import com.example.mrquiz.dto.auth.UserResponseDto;
import com.example.mrquiz.dto.auth.UserUpdateDto;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.entity.file.File;
import com.example.mrquiz.mapper.BaseMapper;
import com.example.mrquiz.repository.file.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends BaseMapper<User, UserCreateDto, UserUpdateDto, UserResponseDto> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileRepository fileRepository;

    public UserMapper() {
        super(User.class, UserCreateDto.class, UserUpdateDto.class, UserResponseDto.class);
    }

    @Override
    protected void afterCreateMapping(UserCreateDto createDto, User entity) {
        // Hash password
        if (createDto.getPassword() != null) {
            entity.setPasswordHash(passwordEncoder.encode(createDto.getPassword()));
        }

        // Set profile image if provided
        if (createDto.getProfileImageId() != null) {
            fileRepository.findById(createDto.getProfileImageId())
                    .ifPresent(entity::setProfileImage);
        }

        // Set default values
        if (entity.getTimezone() == null) {
            entity.setTimezone("UTC");
        }
        if (entity.getLanguage() == null) {
            entity.setLanguage("en");
        }
    }

    @Override
    protected void afterUpdateMapping(UserUpdateDto updateDto, User entity) {
        // Set profile image if provided
        if (shouldUpdate(updateDto.getProfileImageId())) {
            if (updateDto.getProfileImageId() != null) {
                fileRepository.findById(updateDto.getProfileImageId())
                        .ifPresent(entity::setProfileImage);
            } else {
                entity.setProfileImage(null);
            }
        }
    }

    @Override
    protected void afterResponseMapping(User entity, UserResponseDto responseDto) {
        // The ModelMapper configuration already handles the profileImageId mapping
        // Additional custom logic can be added here if needed
    }

    /**
     * Special method for password updates
     */
    public void updatePassword(User user, String newPassword) {
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }
    }

    /**
     * Create a minimal user response for public display
     */
    public UserResponseDto toPublicResponseDto(User entity) {
        if (entity == null) {
            return null;
        }
        
        UserResponseDto dto = new UserResponseDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setRole(entity.getRole());
        dto.setProfileImageId(entity.getProfileImage() != null ? entity.getProfileImage().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        
        return dto;
    }

    /**
     * Verify password against hash
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}