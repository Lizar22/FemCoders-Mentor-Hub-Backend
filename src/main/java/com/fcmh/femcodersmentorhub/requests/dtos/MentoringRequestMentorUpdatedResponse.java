package com.fcmh.femcodersmentorhub.requests.dtos;

import com.fcmh.femcodersmentorhub.requests.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MentoringRequestMentorUpdatedResponse(
        @NotNull(message = "Changing the status is mandatory")
        RequestStatus status,

        @Size(max = 1000, message = "The message cannot exceed 1000 characters")
        String responseMessage,

        @Pattern(regexp = "https?://.*", message = "Meeting link must be a valid URL")
        String meetingLink
) {
}