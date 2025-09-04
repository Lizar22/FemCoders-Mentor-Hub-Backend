package com.fcmh.femcodersmentorhub.request.service;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.emails.EmailService;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.exceptions.MentorProfileNotFoundException;
import com.fcmh.femcodersmentorhub.mentors.repository.MentorRepository;
import com.fcmh.femcodersmentorhub.requests.MentoringRequest;
import com.fcmh.femcodersmentorhub.requests.exceptions.InvalidMentoringRequestException;
import com.fcmh.femcodersmentorhub.requests.exceptions.MentoringRequestNotFoundException;
import com.fcmh.femcodersmentorhub.requests.exceptions.UnauthorizedMentoringRequestException;
import com.fcmh.femcodersmentorhub.requests.repository.MentoringRequestRepository;
import com.fcmh.femcodersmentorhub.requests.RequestStatus;
import com.fcmh.femcodersmentorhub.requests.SessionDuration;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMenteeRequest;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMentorUpdatedResponse;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestResponse;
import com.fcmh.femcodersmentorhub.requests.services.MentoringRequestServiceImpl;
import com.fcmh.femcodersmentorhub.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentoringRequestServiceImplTest {

    private static final String TEST_MENTEE_USERNAME = "ana.mentee";
    private static final String TEST_MENTEE_EMAIL = "ana@test.com";
    private static final String TEST_MENTOR_USERNAME = "cris.mentor";
    private static final String TEST_MENTOR_EMAIL = "cris.mouta@test.com";
    private static final String TEST_MENTOR_FULL_NAME = "Cris Mouta";
    private static final String TEST_TOPIC = "Controllers in Java with Spring Boot";
    private static final String TEST_RESPONSE_MESSAGE = "Perfect! See you tomorrow";
    private static final String TEST_MEETING_LINK = "https://meet.google.com/abc-def";
    private static final Long TEST_MENTOR_PROFILE_ID = 1L;
    private static final Long TEST_REQUEST_ID = 1L;

    @Mock
    private MentoringRequestRepository mentoringRequestRepository;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MentoringRequestServiceImpl mentoringRequestService;

    private UserAuth menteeUser;
    private UserAuth mentorUser;
    private MentorProfile mentorProfile;
    private MentoringRequest mentoringRequest;
    private MentoringRequestMenteeRequest validMenteeRequest;
    private MentoringRequestMentorUpdatedResponse validMentorResponse;
    private MentoringRequestResponse mentoringRequestResponse;

    @BeforeEach
    void setUp() {

        setupUsers();
        setupMentorProfile();
        setupMentoringRequest();
        setupRequestDTOs();
    }

    @Test
    @DisplayName("GET /mentoring-requests - should return requests when user is mentee")
    void getMyMentoringRequests_WhenUserIsMentee_ReturnsOkAndMenteeRequests() {

        mockAuthenticatedUser(TEST_MENTEE_USERNAME, menteeUser);

        List<MentoringRequestResponse> expectedResponses = List.of(mentoringRequestResponse);

        when(mentoringRequestRepository.findByMentee(menteeUser)).thenReturn(expectedResponses);

        List<MentoringRequestResponse> result = mentoringRequestService.getMyMentoringRequests(authentication);

        assertThat(result).isEqualTo(expectedResponses);

        verify(mentoringRequestRepository).findByMentee(menteeUser);
        verify(mentoringRequestRepository, never()).findByMentorProfile(any());
    }

    @Test
    @DisplayName("GET /mentoring-requests - should return requests when user is mentor")
    void getMyMentoringRequests_WhenUserIsMentor_ReturnsOkAndMentorRequests() {

        mockAuthenticatedUser(TEST_MENTOR_USERNAME, mentorUser);

        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.of(mentorProfile));

        List<MentoringRequestResponse> expectedResponses = List.of(mentoringRequestResponse);
        when(mentoringRequestRepository.findByMentorProfile(mentorProfile)).thenReturn(expectedResponses);

        List<MentoringRequestResponse> result = mentoringRequestService.getMyMentoringRequests(authentication);

        assertThat(result).isEqualTo(expectedResponses);

        verify(mentoringRequestRepository).findByMentorProfile(mentorProfile);
        verify(mentoringRequestRepository, never()).findByMentee(any());
    }


    @Test
    @DisplayName("GET /mentoring-requests - should throw exception when mentor profile not found")
    void getMyMentoringRequests_WhenMentorProfileNotFound_ShouldThrowException() {

        mockAuthenticatedUser(TEST_MENTOR_USERNAME, mentorUser);

        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentoringRequestService.getMyMentoringRequests(authentication))
                .isInstanceOf(MentorProfileNotFoundException.class)
                .hasMessage("Mentor profile not found");
    }

    @Test
    @DisplayName("GET /mentoring-requests - should return empty list for invalid roles")
    void getMyMentoringRequests_WhenInvalidRole_ShouldReturnEmptyList() {

        UserAuth adminUser = createUser(3L, "admin.user", "admin@test.com", Role.ADMIN);

        mockAuthenticatedUser("admin.user", adminUser);

        List<MentoringRequestResponse> result = mentoringRequestService.getMyMentoringRequests(authentication);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("POST /mentoring-requests - should create request successfully with valid data")
    void addMentoringRequest_WhenValidRequest_ShouldCreateAndReturnRequest() {

        mockAuthenticatedUser(TEST_MENTEE_USERNAME, menteeUser);

        when(mentorRepository.findById(TEST_MENTOR_PROFILE_ID)).thenReturn(Optional.of(mentorProfile));
        when(mentoringRequestRepository.existsByMenteeAndMentorProfileAndStatus(
                menteeUser, mentorProfile, RequestStatus.PENDING)).thenReturn(false);
        when(mentoringRequestRepository.save(any(MentoringRequest.class))).thenReturn(mentoringRequest);

        MentoringRequestResponse result = mentoringRequestService.addMentoringRequest(
                validMenteeRequest, authentication);

        assertThat(result).isNotNull();

        verify(mentoringRequestRepository).save(any(MentoringRequest.class));
        verifyEmailSentToMentor();
    }

    @Test
    @DisplayName("POST /mentoring-requests - should fail when user is not mentee")
    void addMentoringRequest_WhenUserIsNotMentee_ShouldThrowException() {

        mockAuthenticatedUser(TEST_MENTOR_USERNAME, mentorUser);

        assertThatThrownBy(() -> mentoringRequestService.addMentoringRequest(
                validMenteeRequest, authentication))
                .isInstanceOf(InvalidMentoringRequestException.class)
                .hasMessage("Invalid credentials");

        verify(mentoringRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST /mentoring-requests - should fail when pending request already exists")
    void addMentoringRequest_WhenPendingRequestExists_ShouldThrowException() {

        mockAuthenticatedUser(TEST_MENTEE_USERNAME, menteeUser);

        when(mentorRepository.findById(TEST_MENTOR_PROFILE_ID)).thenReturn(Optional.of(mentorProfile));
        when(mentoringRequestRepository.existsByMenteeAndMentorProfileAndStatus(
                menteeUser, mentorProfile, RequestStatus.PENDING)).thenReturn(true);

        assertThatThrownBy(() -> mentoringRequestService.addMentoringRequest(
                validMenteeRequest, authentication))
                .isInstanceOf(InvalidMentoringRequestException.class)
                .hasMessage("You already have a pending request with this mentor");

        verify(mentoringRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST /mentoring-requests - should fail when mentor profile does not exist")
    void addMentoringRequest_WhenMentorProfileNotFound_ShouldThrowException() {

        mockAuthenticatedUser(TEST_MENTEE_USERNAME, menteeUser);

        when(mentorRepository.findById(TEST_MENTOR_PROFILE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentoringRequestService.addMentoringRequest(
                validMenteeRequest, authentication))
                .isInstanceOf(MentorProfileNotFoundException.class)
                .hasMessage("Mentor profile not found");

        verify(mentoringRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - should update request successfully with valid response")
    void respondToRequest_WhenValidResponse_ShouldUpdateAndReturnRequest() {

        mockAuthenticatedUser(TEST_MENTOR_USERNAME, mentorUser);

        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.of(mentorProfile));
        when(mentoringRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(mentoringRequest));
        when(mentoringRequestRepository.save(mentoringRequest)).thenReturn(mentoringRequest);

        MentoringRequestResponse result = mentoringRequestService.respondToRequest(
                TEST_REQUEST_ID, validMentorResponse, authentication);

        assertThat(result).isNotNull();
        assertThat(mentoringRequest.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
        assertThat(mentoringRequest.getResponseMessage()).isEqualTo(TEST_RESPONSE_MESSAGE);
        assertThat(mentoringRequest.getMeetingLink()).isEqualTo(TEST_MEETING_LINK);

        verifyEmailSentToMentee();
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - should fail when user is not mentor")
    void respondToRequest_WhenUserIsNotMentor_ShouldThrowException() {

        mockAuthenticatedUser(TEST_MENTEE_USERNAME, menteeUser);

        assertThatThrownBy(() -> mentoringRequestService.respondToRequest(
                TEST_REQUEST_ID, validMentorResponse, authentication))
                .isInstanceOf(UnauthorizedMentoringRequestException.class)
                .hasMessage("Only mentors can respond to requests");

        verify(mentoringRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - should fail when request does not belong to mentor")
    void respondToRequest_WhenUnauthorizedMentor_ShouldThrowException() {

        MentorProfile otherMentorProfile = new MentorProfile();
        otherMentorProfile.setId(99L);
        mentoringRequest.setMentorProfile(otherMentorProfile);

        mockAuthenticatedUser(TEST_MENTOR_USERNAME, mentorUser);

        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.of(mentorProfile));
        when(mentoringRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(mentoringRequest));

        assertThatThrownBy(() -> mentoringRequestService.respondToRequest(
                TEST_REQUEST_ID, validMentorResponse, authentication))
                .isInstanceOf(UnauthorizedMentoringRequestException.class)
                .hasMessage("You do not have permission to respond to this request");
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - should fail when request has already been answered")
    void respondToRequest_WhenRequestAlreadyAnswered_ShouldThrowException() {

        mentoringRequest.setStatus(RequestStatus.ACCEPTED);

        mockAuthenticatedUser(TEST_MENTOR_USERNAME, mentorUser);

        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.of(mentorProfile));
        when(mentoringRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(mentoringRequest));

        assertThatThrownBy(() -> mentoringRequestService.respondToRequest(
                TEST_REQUEST_ID, validMentorResponse, authentication))
                .isInstanceOf(InvalidMentoringRequestException.class)
                .hasMessage("This request has already been answered");
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - should fail when request is not found")
    void respondToRequest_WhenRequestNotFound_ShouldThrowException() {

        mockAuthenticatedUser(TEST_MENTOR_USERNAME, mentorUser);

        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.of(mentorProfile));
        when(mentoringRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentoringRequestService.respondToRequest(
                TEST_REQUEST_ID, validMentorResponse, authentication))
                .isInstanceOf(MentoringRequestNotFoundException.class)
                .hasMessage("Mentoring request with id " + TEST_REQUEST_ID + " not found");
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - should process rejection response correctly")
    void respondToRequest_WhenRequestRejected_ShouldProcessCorrectly() {

        MentoringRequestMentorUpdatedResponse rejectedResponse =
                new MentoringRequestMentorUpdatedResponse(
                        RequestStatus.REJECTED, "I don't have availability", null);

        mockAuthenticatedUser(TEST_MENTOR_USERNAME, mentorUser);

        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.of(mentorProfile));
        when(mentoringRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(mentoringRequest));
        when(mentoringRequestRepository.save(mentoringRequest)).thenReturn(mentoringRequest);

        MentoringRequestResponse result = mentoringRequestService.respondToRequest(
                TEST_REQUEST_ID, rejectedResponse, authentication);

        assertThat(result).isNotNull();
        assertThat(mentoringRequest.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(mentoringRequest.getResponseMessage()).isEqualTo("I don't have availability");
        assertThat(mentoringRequest.getMeetingLink()).isNull();
    }

    private void setupUsers() {
        menteeUser = createUser(1L, TEST_MENTEE_USERNAME, TEST_MENTEE_EMAIL, Role.MENTEE);
        mentorUser = createUser(2L, TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, Role.MENTOR);
    }

    private UserAuth createUser(Long id, String username, String email, Role role) {
        UserAuth user = new UserAuth();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }

    private void setupMentorProfile() {
        mentorProfile = new MentorProfile();
        mentorProfile.setId(TEST_MENTOR_PROFILE_ID);
        mentorProfile.setUser(mentorUser);
        mentorProfile.setFullName(TEST_MENTOR_FULL_NAME);
    }

    private void setupMentoringRequest() {
        mentoringRequest = new MentoringRequest();
        mentoringRequest.setId(TEST_REQUEST_ID);
        mentoringRequest.setMentee(menteeUser);
        mentoringRequest.setMentorProfile(mentorProfile);
        mentoringRequest.setStatus(RequestStatus.PENDING);
        mentoringRequest.setTopic(TEST_TOPIC);
        mentoringRequest.setScheduledAt(LocalDateTime.now().plusDays(1));
    }

    private void setupRequestDTOs() {

        validMenteeRequest = new MentoringRequestMenteeRequest(
                TEST_TOPIC, LocalDateTime.now().plusDays(1), SessionDuration.LONG, TEST_MENTOR_PROFILE_ID
        );

        validMentorResponse = new MentoringRequestMentorUpdatedResponse(
                RequestStatus.ACCEPTED, TEST_RESPONSE_MESSAGE, TEST_MEETING_LINK
        );

        mentoringRequestResponse = new MentoringRequestResponse(
                TEST_REQUEST_ID, TEST_TOPIC, LocalDateTime.now().plusDays(1), SessionDuration.LONG, RequestStatus.PENDING, TEST_RESPONSE_MESSAGE, TEST_MEETING_LINK, TEST_MENTEE_USERNAME
        );
    }

    private void mockAuthenticatedUser(String username, UserAuth user) {
        when(authentication.getName()).thenReturn(username);
        when(userAuthRepository.findByUsername(username)).thenReturn(Optional.of(user));
    }

    private void verifyEmailSentToMentor() {
        verify(emailService).sendRequestNotificationToMentor(
                eq(TEST_MENTOR_EMAIL), eq(TEST_MENTOR_FULL_NAME), eq(TEST_MENTEE_USERNAME),
                eq(TEST_TOPIC), anyString()
        );
    }

    private void verifyEmailSentToMentee() {
        verify(emailService).sendResponseNotificationToMentee(
                eq(TEST_MENTEE_EMAIL), eq(TEST_MENTEE_USERNAME), eq(TEST_MENTOR_FULL_NAME),
                eq(TEST_TOPIC), eq("ACCEPTED"), eq(TEST_RESPONSE_MESSAGE), eq(TEST_MEETING_LINK)
        );
    }
}

