package com.fcmh.femcodersmentorhub.mentor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcmh.femcodersmentorhub.mentors.Level;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.repository.MentorRepository;
import com.fcmh.femcodersmentorhub.security.Role;
import com.fcmh.femcodersmentorhub.utils.ApiSuccessResponseTestHelper;
import com.fcmh.femcodersmentorhub.utils.UserTestHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MentorControllerIntegrationTest {

    private static final String BASE_URL = "/api/mentors";

    private static final String TEST_MENTOR_USERNAME = "cris.mentor";
    private static final String TEST_MENTOR_EMAIL = "cris.mentor@test.com";
    private static final String TEST_PASSWORD = "Password123.";
    private static final String TEST_MENTOR_FULL_NAME = "Cris Mentor";
    private static final List<String> TEST_TECHNOLOGIES = List.of("Java", "Spring Boot");
    private static final Level TEST_LEVEL = Level.MID;
    private static final String UPDATED_FULL_NAME = "Cris Mouta Updated";
    private static final List<String> UPDATED_TECHNOLOGIES = List.of("Spring Boot", "React");
    private static final Level UPDATED_LEVEL = Level.SENIOR;
    private static final String MSG_LIST = "Mentor profiles list retrieved successfully";
    private static final String MSG_RETRIEVED = "Mentor profile retrieved successfully";
    private static final String MSG_CREATED = "Mentor profile created successfully";
    private static final String MSG_UPDATED = "Mentor profile updated successfully";
    private static final String MSG_DELETED = "Mentor profile deleted successfully";
    private static final String TEST_BIO = "I love mentoring juniors!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private UserTestHelper userTestHelper;

    private ApiSuccessResponseTestHelper apiHelper;

    @BeforeEach
    void setUp() {

        apiHelper = new ApiSuccessResponseTestHelper(mockMvc, objectMapper);
        mentorRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /mentors - should return list of mentors")
    void getAllMentors_ShouldReturnMentorList() throws Exception {

        MentorProfile mentor = userTestHelper.existingMentor(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD,
                TEST_MENTOR_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO
        );

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Mentor profiles list retrieved successfully"))
                .andExpect(jsonPath("$.data[0].fullName").value(TEST_MENTOR_FULL_NAME));
    }

    @Test
    @DisplayName("GET /mentors - should return empty list when no mentors exist")
    void getAllMentors_WhenNoneExist_ShouldReturnEmptyList() throws Exception {

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MSG_LIST))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET /mentors - should return mentors with filters")
    void getAllMentors_WhenFiltersApplied_ShouldReturnFilteredMentors() throws Exception {

        userTestHelper.existingMentor(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD,
                TEST_MENTOR_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO
        );

        mockMvc.perform(get(BASE_URL)
                        .param("technologies", "Java")
                        .param("levels", TEST_LEVEL.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MSG_LIST))
                .andExpect(jsonPath("$.data[0].fullName").value(TEST_MENTOR_FULL_NAME));
    }

    @Test
    @DisplayName("GET /mentors/{id} - should return mentor profile when exists")
    void getMentorProfileById_WhenExists_ShouldReturnProfile() throws Exception {

        MentorProfile mentor = userTestHelper.existingMentor(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD,
                TEST_MENTOR_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO
        );

        mockMvc.perform(get(BASE_URL + "/" + mentor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MSG_RETRIEVED))
                .andExpect(jsonPath("$.data.fullName").value(TEST_MENTOR_FULL_NAME));
    }

    @Test
    @DisplayName("GET /mentors/{id} - should return 404 when mentor not found")
    void getMentorProfileById_WhenNotFound_ShouldReturn404() throws Exception {

        mockMvc.perform(get(BASE_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("AUTH_01"))
                .andExpect(jsonPath("$.message").value(containsString("User not found with id 999")))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @WithMockUser(username = TEST_MENTOR_USERNAME, roles = {"MENTOR"})
    @DisplayName("POST /mentors - should create mentor profile successfully")
    void addMentorProfile_WhenValid_ShouldReturnCreated() throws Exception {

        userTestHelper.existingUser(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD, Role.MENTOR
        );

        MentorRequest request = new MentorRequest(TEST_MENTOR_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO);

        apiHelper.performRequest(post(BASE_URL), request, MSG_CREATED)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.fullName").value(TEST_MENTOR_FULL_NAME))
                .andExpect(jsonPath("$.data.technologies").isArray())
                .andExpect(jsonPath("$.data.level").value(TEST_LEVEL.name()));
    }

    @Test
    @WithMockUser(username = TEST_MENTOR_USERNAME, roles = {"MENTOR"})
    @DisplayName("POST /mentors - should fail when mentor profile already exists")
    void addMentorProfile_WhenExists_ShouldReturnConflict() throws Exception {

        userTestHelper.existingMentor(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD, TEST_MENTOR_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO
        );

        MentorRequest request = new MentorRequest(TEST_MENTOR_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO);

        apiHelper.performErrorRequest(post(BASE_URL),
                request,
                "MENTOR_02",
                409,
                "A mentor profile already exists for this user");
    }

    @Test
    @WithMockUser(username = TEST_MENTOR_USERNAME, roles = {"MENTOR"})
    @DisplayName("PUT /mentors/me - should update mentor profile successfully")
    void updateMentorProfile_WhenValid_ShouldReturnUpdatedProfile() throws Exception {

        userTestHelper.existingMentor(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD,
                TEST_MENTOR_FULL_NAME, TEST_TECHNOLOGIES, Level.JUNIOR, TEST_BIO
        );

        MentorRequest updatedRequest = new MentorRequest(UPDATED_FULL_NAME, UPDATED_TECHNOLOGIES, UPDATED_LEVEL, TEST_BIO);

        apiHelper.performRequest(put(BASE_URL + "/me"), updatedRequest, MSG_UPDATED)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value(UPDATED_FULL_NAME))
                .andExpect(jsonPath("$.data.level").value(UPDATED_LEVEL.name()));
    }

    @Test
    @WithMockUser(username = TEST_MENTOR_USERNAME, roles = {"MENTOR"})
    @DisplayName("PUT /mentors/me - should fail when mentor profile not found")
    void updateMentorProfile_WhenNotFound_ShouldReturn404() throws Exception {

        userTestHelper.existingUser(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD, Role.MENTOR
        );

        MentorRequest updatedRequest = new MentorRequest(UPDATED_FULL_NAME, UPDATED_TECHNOLOGIES, UPDATED_LEVEL, TEST_BIO);

        apiHelper.performErrorRequest(put(BASE_URL + "/me"),
                updatedRequest,
                "MENTOR_01",
                404,
                "Mentor profile not found");
    }

    @Test
    @WithMockUser(username = TEST_MENTOR_USERNAME, roles = {"MENTOR"})
    @DisplayName("DELETE /mentors/me - should delete mentor profile successfully")
    void deleteMentorProfile_WhenExists_ShouldReturnOk() throws Exception {

        userTestHelper.existingMentor(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD,
                TEST_MENTOR_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO
        );

        mockMvc.perform(delete(BASE_URL + "/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MSG_DELETED));
    }

    @Test
    @WithMockUser(username = TEST_MENTOR_USERNAME, roles = {"MENTOR"})
    @DisplayName("DELETE /mentors/me - should fail when mentor profile not found")
    void deleteMentorProfile_WhenNotFound_ShouldReturn404() throws Exception {

        userTestHelper.existingUser(
                TEST_MENTOR_USERNAME, TEST_MENTOR_EMAIL, TEST_PASSWORD, Role.MENTOR
        );

        mockMvc.perform(delete(BASE_URL + "/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(notNullValue()))
                .andExpect(jsonPath("$.message", containsString("Mentor profile not found")));
    }
}
