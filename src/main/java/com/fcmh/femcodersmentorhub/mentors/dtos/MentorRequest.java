package com.fcmh.femcodersmentorhub.mentors.dtos;

import com.fcmh.femcodersmentorhub.mentors.Level;
import jakarta.validation.constraints.*;

import java.util.List;

public record MentorRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 60, message = "Username must contain between 2 and 60 characters")
        String fullName,

        @NotEmpty(message = "You must provide at least one technology")
        @Size(min = 1, max = 10, message = "You must provide between 1 and 10 technologies")
        List<   @NotBlank(message = "Technology cannot be blank")
                @Size(max = 30, message = "Technology name cannot exceed 30 characters")
                @Pattern(regexp = "^[a-zA-Z0-9\\-. ]+$", message = "Technology can only contain letters, numbers, hyphens and spaces")
                String> technologies,

        @NotNull(message = "Level is required")
        Level level,

        @Size(max = 1000, message = "Biography must not exceed 1000 characters")
        String bio
) {
}