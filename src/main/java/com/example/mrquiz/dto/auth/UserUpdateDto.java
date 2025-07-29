package com.example.mrquiz.dto.auth;

import com.example.mrquiz.enums.UserRole;
import com.example.mrquiz.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class UserUpdateDto {
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    private UserRole role;
    private UserStatus status;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    private Boolean phoneVerified;
    private Boolean mfaEnabled;
    private UUID profileImageId;

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;

    @Size(max = 10, message = "Language must not exceed 10 characters")
    private String language;

    private Map<String, Object> metadata;
    private Map<String, Object> preferences;
}