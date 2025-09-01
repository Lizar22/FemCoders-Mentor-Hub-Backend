package com.fcmh.femcodersmentorhub.requests.dtos;

import com.fcmh.femcodersmentorhub.requests.SessionDuration;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record MentoringRequestMenteeRequest(
        @NotBlank(message = "The topic is mandatory")
        @Size(min = 5, max = 100, message = "The topic must contain between 5 and 100 characters")
        String topic,

        @NotNull(message = "The date and time are required")
        @Future(message = "The date must be set for the future")
        LocalDateTime scheduledAt,

        @NotNull(message = "The duration of the session is required")
        SessionDuration sessionDuration,

        @NotNull(message = "The mentor profile ID is required")
        @Positive(message = "The ID must be a positive number")
        Long mentorProfileId
) {
}