package com.fcmh.femcodersmentorhub.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcmh.femcodersmentorhub.requests.RequestStatus;
import com.fcmh.femcodersmentorhub.requests.SessionDuration;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMenteeRequest;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMentorUpdatedResponse;
import com.fcmh.femcodersmentorhub.requests.services.MentoringRequestServiceImpl;
import com.fcmh.femcodersmentorhub.utils.ApiSuccessResponseTestHelper;
import com.fcmh.femcodersmentorhub.utils.UserTestHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MentoringRequestControllerIntegrationTest {

    private static final String BASE_URL = "/api/mentoring-requests";
    private static final String TEST_MENTEE_USERNAME = "ana.mentee";
    private static final String TEST_MENTOR_USERNAME = "cris.mentor";
    private static final String TEST_OTHER_MENTOR_USERNAME = "other.mentor";
    private static final String TEST_TOPIC = "Java Backend Mentorship";
    private static final String TEST_TOPIC_UPDATED = "Spring Boot Guidance";
    private static final String TEST_MEETING_LINK = "https://meet.google.com/abc-def";
    private static final String MSG_LIST = "Mentoring requests list retrieved successfully";
    private static final String MSG_CREATED = "Mentoring request created successfully";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserTestHelper userTestHelper;

    @Autowired
    private MentoringRequestServiceImpl mentoringRequestService;

    private ApiSuccessResponseTestHelper apiHelper;

    private Long mentorProfileId;
    private Long anotherMentorProfileId;

    @BeforeEach
    void setUp() {
        apiHelper = new ApiSuccessResponseTestHelper(mockMvc, objectMapper);
        UserTestHelper.TestUsers testUsers = userTestHelper.createDefaultTestUsers();
        mentorProfileId = testUsers.mentorProfileId();
        anotherMentorProfileId = testUsers.anotherMentorProfileId();
    }

    @Test
    @DisplayName("GET /mentoring-requests - should return list of my mentoring requests")
    @WithMockUser(username = TEST_MENTEE_USERNAME, roles = {"MENTEE"})
    void getMyMentoringRequests_ReturnsList() throws Exception {

        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        apiHelper.performRequest(post(BASE_URL), request, MSG_CREATED, HttpStatus.CREATED);

        apiHelper.performRequest(get(BASE_URL), null, MSG_LIST)
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].topic").value(TEST_TOPIC));
    }

    @Test
    @DisplayName("GET /mentoring-requests - unauthorized")
    void getMyMentoringRequests_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /mentoring-requests - should create a mentoring request")
    @WithMockUser(username = TEST_MENTEE_USERNAME, roles = {"MENTEE"})
    void createMentoringRequest_ShouldSucceed() throws Exception {

        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        ResultActions result = apiHelper.performRequest(
                post(BASE_URL),
                request,
                MSG_CREATED,
                HttpStatus.CREATED
        );

        result.andExpect(jsonPath("$.data.topic").value(TEST_TOPIC));
    }

    @Test
    @DisplayName("GET /mentoring-requests - should return empty list when no requests")
    @WithMockUser(username = TEST_MENTEE_USERNAME, roles = {"MENTEE"})
    void getMyMentoringRequests_EmptyList_ShouldReturnEmptyArray() throws Exception {

        apiHelper.performRequest(get(BASE_URL), null, MSG_LIST)
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET /mentoring-requests - should return multiple requests with different statuses")
    @WithMockUser(username = TEST_MENTEE_USERNAME, roles = {"MENTEE"})
    void getMyMentoringRequests_MultipleRequests_ShouldReturnAllRequests() throws Exception {

        MentoringRequestMenteeRequest firstRequest = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        apiHelper.performRequest(post(BASE_URL), firstRequest, MSG_CREATED, HttpStatus.CREATED);

        MentoringRequestMenteeRequest secondRequest = new MentoringRequestMenteeRequest(
                TEST_TOPIC_UPDATED,
                LocalDateTime.now().plusDays(3),
                SessionDuration.SHORT,
                anotherMentorProfileId
        );

        apiHelper.performRequest(post(BASE_URL), secondRequest, MSG_CREATED, HttpStatus.CREATED);

        apiHelper.performRequest(get(BASE_URL), null, MSG_LIST)
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].topic").value(anyOf(equalTo(TEST_TOPIC), equalTo(TEST_TOPIC_UPDATED))))
                .andExpect(jsonPath("$.data[1].topic").value(anyOf(equalTo(TEST_TOPIC), equalTo(TEST_TOPIC_UPDATED))))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data[1].status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /mentoring-requests - mentor not found")
    @WithMockUser(username = TEST_MENTEE_USERNAME, roles = {"MENTEE"})
    void createMentoringRequest_MentorNotFound_ShouldReturn404() throws Exception {
        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                999L
        );

        apiHelper.performErrorRequest(
                post(BASE_URL),
                request,
                "MENTOR_01",
                404,
                "Mentor profile not found"
        );
    }

    @Test
    @DisplayName("POST /mentoring-requests - duplicate pending request")
    @WithMockUser(username = TEST_MENTEE_USERNAME, roles = {"MENTEE"})
    void createMentoringRequest_Duplicate_ShouldReturn400() throws Exception {

        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        apiHelper.performRequest(post(BASE_URL), request, MSG_CREATED, HttpStatus.CREATED);

        apiHelper.performErrorRequest(
                post(BASE_URL),
                request,
                "REQUEST_01",
                400,
                "You already have a pending request with this mentor"
        );
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - respond successfully")
    void respondToRequest_ShouldSucceed() throws Exception {

        Long requestId;

        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        String responseJson = mockMvc.perform(post(BASE_URL)
                        .with(user(TEST_MENTEE_USERNAME).roles("MENTEE"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        requestId = objectMapper.readTree(responseJson).get("data").get("id").asLong();

        MentoringRequestMentorUpdatedResponse mentorResponse =
                new MentoringRequestMentorUpdatedResponse(RequestStatus.ACCEPTED, "Confirmed", TEST_MEETING_LINK);

        mockMvc.perform(put(BASE_URL + "/" + requestId + "/respond")
                        .with(user(TEST_MENTOR_USERNAME).roles("MENTOR"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mentorResponse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - not assigned mentor")
    void respondToRequest_NotAssigned_ShouldReturn403() throws Exception {

        Long requestId;

        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        String responseJson = mockMvc.perform(post(BASE_URL)
                        .with(user(TEST_MENTEE_USERNAME).roles("MENTEE"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        requestId = objectMapper.readTree(responseJson).get("data").get("id").asLong();

        MentoringRequestMentorUpdatedResponse mentorResponse =
                new MentoringRequestMentorUpdatedResponse(RequestStatus.ACCEPTED, "Confirmed", TEST_MEETING_LINK);

        mockMvc.perform(put(BASE_URL + "/" + requestId + "/respond")
                        .with(user(TEST_OTHER_MENTOR_USERNAME).roles("MENTOR"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mentorResponse)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have permission to respond to this request"));
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - already answered")
    void respondToRequest_AlreadyAnswered_ShouldReturn400() throws Exception {

        Long requestId;

        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        String responseJson = mockMvc.perform(post(BASE_URL)
                        .with(user(TEST_MENTEE_USERNAME).roles("MENTEE"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        requestId = objectMapper.readTree(responseJson).get("data").get("id").asLong();

        MentoringRequestMentorUpdatedResponse mentorResponse =
                new MentoringRequestMentorUpdatedResponse(RequestStatus.ACCEPTED, "Confirmed", TEST_MEETING_LINK);

        mockMvc.perform(put(BASE_URL + "/" + requestId + "/respond")
                        .with(user(TEST_MENTOR_USERNAME).roles("MENTOR"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mentorResponse)))
                .andExpect(status().isOk());

        mockMvc.perform(put(BASE_URL + "/" + requestId + "/respond")
                        .with(user(TEST_MENTOR_USERNAME).roles("MENTOR"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mentorResponse)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This request has already been answered"));
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - request not found")
    void respondToRequest_NotFound_ShouldReturn404() throws Exception {

        MentoringRequestMentorUpdatedResponse mentorResponse =
                new MentoringRequestMentorUpdatedResponse(RequestStatus.ACCEPTED, "Confirmed", TEST_MEETING_LINK);

        mockMvc.perform(put(BASE_URL + "/" + 999L + "/respond")
                        .with(user(TEST_MENTOR_USERNAME).roles("MENTOR"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mentorResponse)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Mentoring request with id 999 not found"));
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - non-mentor user")
    void respondToRequest_NonMentor_ShouldReturn403() throws Exception {

        Long requestId = 1L;

        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        MentoringRequestMentorUpdatedResponse mentorResponse =
                new MentoringRequestMentorUpdatedResponse(RequestStatus.ACCEPTED, "Confirmed", TEST_MEETING_LINK);

        mockMvc.perform(put(BASE_URL + "/" + requestId + "/respond")
                        .with(user(TEST_MENTEE_USERNAME).roles("MENTEE"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mentorResponse)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /mentoring-requests/{id}/respond - invalid data")
    void respondToRequest_InvalidData_ShouldReturn400() throws Exception {

        Long requestId;

        MentoringRequestMenteeRequest request = new MentoringRequestMenteeRequest(
                TEST_TOPIC,
                LocalDateTime.now().plusDays(2),
                SessionDuration.LONG,
                mentorProfileId
        );

        String responseJson = mockMvc.perform(post(BASE_URL)
                        .with(user(TEST_MENTEE_USERNAME).roles("MENTEE"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        requestId = objectMapper.readTree(responseJson).get("data").get("id").asLong();

        String invalidJson = """
        {
            "status": null,
            "notes": "Test notes",
            "meetingLink": "https://meet.google.com/test"
        }
        """;

        mockMvc.perform(put(BASE_URL + "/" + requestId + "/respond")
                        .with(user(TEST_MENTOR_USERNAME).roles("MENTOR"))
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
