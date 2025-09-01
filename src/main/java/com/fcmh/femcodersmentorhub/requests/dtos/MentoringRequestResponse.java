package com.fcmh.femcodersmentorhub.requests.dtos;

import com.fcmh.femcodersmentorhub.requests.RequestStatus;
import com.fcmh.femcodersmentorhub.requests.SessionDuration;

import java.time.LocalDateTime;

public record MentoringRequestResponse(
        Long id,
        String topic,
        LocalDateTime scheduledAt,
        SessionDuration sessionDuration,
        RequestStatus status,
        String responseMessage,
        String meetingLink,
        String menteeUsername
) {
}