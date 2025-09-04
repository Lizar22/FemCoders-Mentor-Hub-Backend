package com.fcmh.femcodersmentorhub.requests.services;

import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMenteeRequest;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMentorUpdatedResponse;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MentoringRequestService {

    List<MentoringRequestResponse> getMyMentoringRequests(Authentication authentication);
    MentoringRequestResponse addMentoringRequest(MentoringRequestMenteeRequest request, Authentication authentication);
    MentoringRequestResponse respondToRequest(Long id, MentoringRequestMentorUpdatedResponse mentorUpdatedResponse, Authentication authentication);
}