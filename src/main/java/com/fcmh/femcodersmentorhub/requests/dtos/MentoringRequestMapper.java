package com.fcmh.femcodersmentorhub.requests.dtos;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.requests.MentoringRequest;
import com.fcmh.femcodersmentorhub.requests.RequestStatus;

public class MentoringRequestMapper {
    public static MentoringRequest dtoToEntity(MentoringRequestMenteeRequest request, UserAuth mentee, MentorProfile mentorProfile) {
        return MentoringRequest.builder()
                .topic(request.topic())
                .scheduledAt(request.scheduledAt())
                .sessionDuration(request.sessionDuration())
                .mentee(mentee)
                .mentorProfile(mentorProfile)
                .status(RequestStatus.PENDING)
                .meetingLink(null)
                .responseMessage(null)
                .build();
    }

    public static MentoringRequestResponse entityToDto(MentoringRequest request) {
        return new MentoringRequestResponse(
                request.getId(),
                request.getTopic(),
                request.getScheduledAt(),
                request.getSessionDuration(),
                request.getStatus(),
                request.getResponseMessage(),
                request.getMeetingLink(),
                request.getMentee().getUsername()
        );
    }
}