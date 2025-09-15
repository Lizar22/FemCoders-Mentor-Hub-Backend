package com.fcmh.femcodersmentorhub.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserAuthControllerIntegrationTest {

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String TEST_USERNAME = "Cris Mouta";
    private static final String TEST_EMAIL = "cris.mouta@fcmh.com";
    private static final String TEST_PASSWORD = "Password123.";
    private static final Role TEST_ROLE = Role.MENTOR;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserTestHelper userTestHelper;

    private ApiSuccessResponseTestHelper apiHelper;

    @BeforeEach
    void setUp() {

        apiHelper = new ApiSuccessResponseTestHelper(mockMvc, objectMapper);
        userAuthRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /register - should add a new user successfully")
    void addUser_WhenValidData_ReturnsOkAndAddedUser() throws Exception {

        UserAuthRequest newUser = new UserAuthRequest(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_ROLE
        );

        apiHelper.performRequest(post(REGISTER_URL), newUser,"User registered successfully", HttpStatus.CREATED)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.role").value(TEST_ROLE.toString()));

        assertTrue(userAuthRepository.existsByEmail(TEST_EMAIL));
        assertTrue(userAuthRepository.existsByUsername(TEST_USERNAME));

        UserAuth saverUser = userAuthRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertNotEquals(TEST_PASSWORD, saverUser.getPassword());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, saverUser.getPassword()));
    }

    @Test
    @DisplayName("POST /register - should return 409 when email already exists")
    void addUser_WhenEmailExists_ReturnsConflict() throws Exception {

        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        UserAuthRequest duplicateEmailUser = new UserAuthRequest(
                "Different User",
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_ROLE
        );

        apiHelper.performErrorRequest(post(REGISTER_URL),
                duplicateEmailUser,
                "AUTH_03",
                409,
                "The email is already registered"
        );
    }

    @Test
    @DisplayName("POST /register - should return 409 when username already exists")
    void addUser_WhenUsernameExists_ReturnsConflict() throws Exception {

        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        UserAuthRequest duplicateUsernameUser = new UserAuthRequest(
                TEST_USERNAME,
                "different.email@fcmh.com",
                TEST_PASSWORD,
                TEST_ROLE
        );

        apiHelper.performErrorRequest(post(REGISTER_URL),
                duplicateUsernameUser,
                "AUTH_03",
                409,
                "The username is already registered"
        );
    }

    @Test
    @DisplayName("POST /register - should return 400 for invalid email")
    void addUser_WhenInvalidEmail_ReturnsBadRequest() throws Exception {

        UserAuthRequest invalidEmailUser = new UserAuthRequest(
                TEST_USERNAME,
                "invalid email",
                TEST_PASSWORD,
                TEST_ROLE
        );

        apiHelper.performErrorRequest(post(REGISTER_URL),
                invalidEmailUser,
                "VALIDATION_01",
                400,
                "Invalid e-mail format"
        );
    }

    @Test
    @DisplayName("POST /register - should return 400 for weak password")
    void addUser_WhenWeakPassword_ReturnsBadRequest() throws Exception {

        UserAuthRequest weakPasswordUser = new UserAuthRequest(
                TEST_USERNAME,
                TEST_EMAIL,
                "weak-password",
                TEST_ROLE
        );

        apiHelper.performErrorRequest(post(REGISTER_URL),
                weakPasswordUser,
                "VALIDATION_01",
                400,
                "Password must contain a minimum of 12 characters, including a number, one uppercase letter, one lowercase letter and one special character"
        );
    }

    @Test
    @DisplayName("POST /login - should login successfully with email")
    void login_WhenValidEmailAndPassword_ReturnsToken() throws Exception {

        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        apiHelper.performRequest(post(LOGIN_URL), loginRequest, "Login successful", HttpStatus.OK)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.token").value(notNullValue()));
    }

    @Test
    @DisplayName("POST /login - should login successfully with username")
    void login_WhenValidUsernameAndPassword_ReturnsToken() throws Exception {

        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        apiHelper.performRequest(post(LOGIN_URL), loginRequest, "Login successful", HttpStatus.OK)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.token").value(notNullValue()));
    }

    @Test
    @DisplayName("POST /login - should return 401 for nonexistent user")
    void login_WhenUserNotFound_ReturnsUnauthorized() throws Exception {

        LoginRequest loginRequest = new LoginRequest("Nonexistent@fcmh.com", TEST_PASSWORD);

        apiHelper.performErrorRequest(post(LOGIN_URL),
                loginRequest,
                "AUTH_02",
                401,
                "Invalid credentials"
        );
    }

    @Test
    @DisplayName("POST /login - should return 401 for wrong password")
    void login_WhenPasswordIsIncorrect_ReturnsUnauthorized() throws Exception {

        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, "WrongPassword123.");

        apiHelper.performErrorRequest(post(LOGIN_URL),
                loginRequest,
                "AUTH_02",
                401,
                "Invalid credentials"
        );
    }

    @Test
    @DisplayName("POST /login - should return 400 for empty identifier")
    void login_WhenEmptyIdentifier_ReturnsBadRequest() throws Exception {

        LoginRequest loginRequest = new LoginRequest("", TEST_PASSWORD);

        apiHelper.performErrorRequest(post(LOGIN_URL),
                loginRequest,
                "VALIDATION_01",
                400,
                "Username or e-mail is required"
        );
    }

    @Test
    @DisplayName("POST /login - should return 400 for empty password")
    void login_WhenEmptyPassword_ReturnsBadRequest() throws Exception {

        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, "");

        apiHelper.performErrorRequest(post(LOGIN_URL),
                loginRequest,
                "VALIDATION_01",
                400,
                "Password is required"
        );
    }
}
