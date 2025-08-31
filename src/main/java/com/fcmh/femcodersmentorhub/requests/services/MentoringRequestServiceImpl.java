package com.fcmh.femcodersmentorhub.requests.services;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.MentorRepository;
import com.fcmh.femcodersmentorhub.mentors.exceptions.MentorProfileNotFoundException;
import com.fcmh.femcodersmentorhub.requests.MentoringRequest;
import com.fcmh.femcodersmentorhub.requests.repository.MentoringRequestRepository;
import com.fcmh.femcodersmentorhub.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentoringRequestServiceImpl implements MentoringRequestService{
    private final MentoringRequestRepository mentoringRequestRepository;
    private final UserAuthRepository userAuthRepository;
    private final MentorRepository mentorRepository;

    @Override
    public List<MentoringRequest> getMyMentoringRequests(Authentication authentication) {
        UserAuth user = getAuthenticatedUser(authentication);

        if (user.getRole() == Role.MENTEE) {
            return mentoringRequestRepository.findByMentee(user);
        }

        if (user.getRole() == Role.MENTOR) {
            MentorProfile mentorProfile = mentorRepository.findByUser(user).orElseThrow(() -> new MentorProfileNotFoundException("Mentor profile not found"));
            return mentoringRequestRepository.findByMentorProfile(mentorProfile);
        }

        return List.of();
    }

    @Override
    public MentoringRequest addMentoringRequest(MentoringRequest mentoringRequest, Authentication authentication) {
        return null;
    }

    private UserAuth getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        return userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}