package com.example.mrquiz.dto.auth;

import com.example.mrquiz.enums.UserRole;
import com.example.mrquiz.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class UserCreateDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotNull(message = "Role is required")
    private UserRole role;

    private UserStatus status = UserStatus.ACTIVE;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    private UUID profileImageId;

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone = "UTC";

    @Size(max = 10, message = "Language must not exceed 10 characters")
    private String language = "en";

    private Map<String, Object> metadata;
    private Map<String, Object> preferences;
}