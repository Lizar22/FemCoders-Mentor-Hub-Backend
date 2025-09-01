package com.fcmh.femcodersmentorhub.requests.services;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.emails.EmailService;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.repository.MentorRepository;
import com.fcmh.femcodersmentorhub.mentors.exceptions.MentorProfileNotFoundException;
import com.fcmh.femcodersmentorhub.requests.MentoringRequest;
import com.fcmh.femcodersmentorhub.requests.RequestStatus;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMapper;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMenteeRequest;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMentorUpdatedResponse;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestResponse;
import com.fcmh.femcodersmentorhub.requests.exceptions.InvalidMentoringRequestException;
import com.fcmh.femcodersmentorhub.requests.exceptions.MentoringRequestNotFoundException;
import com.fcmh.femcodersmentorhub.requests.exceptions.UnauthorizedMentoringRequestException;
import com.fcmh.femcodersmentorhub.requests.repository.MentoringRequestRepository;
import com.fcmh.femcodersmentorhub.security.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MentoringRequestServiceImpl implements MentoringRequestService{

    private static final String DATE_TIME_PATTERN = "dd/MM/yyyy 'a las' HH:mm";

    private final MentoringRequestRepository mentoringRequestRepository;
    private final UserAuthRepository userAuthRepository;
    private final MentorRepository mentorRepository;
    private final EmailService emailService;

    @Override
    public List<MentoringRequestResponse> getMyMentoringRequests(Authentication authentication) {
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
    public MentoringRequestResponse addMentoringRequest(MentoringRequestMenteeRequest request, Authentication authentication) {
        UserAuth mentee = getAuthenticatedUser(authentication);

        if (mentee.getRole() != Role.MENTEE) {
            throw new InvalidMentoringRequestException("Invalid credentials");
        }

        MentorProfile mentorProfile = mentorRepository.findById(request.mentorProfileId()).orElseThrow(() -> new MentorProfileNotFoundException("Mentor profile not found"));

        boolean existsPendingRequest = mentoringRequestRepository.existsByMenteeAndMentorProfileAndStatus(
                mentee,
                mentorProfile,
                RequestStatus.PENDING
        );

        if (existsPendingRequest) {
            throw  new InvalidMentoringRequestException("You already have a pending request with this mentor");
        }

        MentoringRequest newMentoringRequest = MentoringRequestMapper.dtoToEntity(request, mentee, mentorProfile);
        MentoringRequest savedRequest = mentoringRequestRepository.save(newMentoringRequest);

        sendRequestNotificationToMentor(savedRequest);

        return MentoringRequestMapper.entityToDto(savedRequest);
    }

    @Override
    public MentoringRequestResponse respondToRequest(Long id, MentoringRequestMentorUpdatedResponse mentorUpdatedResponse, Authentication authentication) {
        UserAuth user = getAuthenticatedUser(authentication);

        if (user.getRole() != Role.MENTOR) {
            throw new UnauthorizedMentoringRequestException("Only mentors can respond to requests");
        }

        MentorProfile mentorProfile = mentorRepository.findByUser(user).orElseThrow(() -> new MentorProfileNotFoundException("Mentor profile not found"));

        MentoringRequest mentoringRequest = mentoringRequestRepository.findById(id).orElseThrow(() -> new MentoringRequestNotFoundException("Mentoring request with id " + id + "not found"));

        if (!mentoringRequest.getMentorProfile().equals(mentorProfile)) {
            throw new UnauthorizedMentoringRequestException("You do not have permission to respond to this request");
        }

        if (mentoringRequest.getStatus() != RequestStatus.PENDING) {
            throw new InvalidMentoringRequestException("This request has already been answered");
        }

        mentoringRequest.setStatus(mentorUpdatedResponse.status());
        mentoringRequest.setResponseMessage(mentorUpdatedResponse.responseMessage());
        mentoringRequest.setMeetingLink(mentorUpdatedResponse.meetingLink());

        MentoringRequest updatedRequest = mentoringRequestRepository.save(mentoringRequest);

        sendResponseNotificationToMentee(updatedRequest);

        return MentoringRequestMapper.entityToDto(updatedRequest);
    }

    private UserAuth getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        return userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private String formatScheduledDate(LocalDateTime scheduledAt) {
        return scheduledAt.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    private void sendRequestNotificationToMentor(MentoringRequest request) {
        try {
            emailService.sendRequestNotificationToMentor(
                    request.getMentorProfile().getUser().getEmail(),
                    request.getMentorProfile().getFullName(),
                    request.getMentee().getUsername(),
                    request.getTopic(),
                    formatScheduledDate(request.getScheduledAt())
            );
        } catch (Exception exception) {
        }
    }

    private void sendResponseNotificationToMentee(MentoringRequest request) {
        try {
            emailService.sendResponseNotificationToMentee(
                    request.getMentee().getEmail(),
                    request.getMentee().getUsername(),
                    request.getMentorProfile().getFullName(),
                    request.getTopic(),
                    request.getStatus().toString(),
                    request.getResponseMessage(),
                    request.getMeetingLink()
            );
        } catch (Exception exception) {
        }
    }
}