package com.fcmh.femcodersmentorhub.requests.services;

import com.fcmh.femcodersmentorhub.requests.MentoringRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MentoringRequestService {
    List<MentoringRequest> getMyMentoringRequests(Authentication authentication);
    MentoringRequest addMentoringRequest(MentoringRequest mentoringRequest, Authentication authentication);
}
