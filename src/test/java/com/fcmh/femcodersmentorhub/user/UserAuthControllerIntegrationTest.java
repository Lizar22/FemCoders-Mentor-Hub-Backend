package com.fcmh.femcodersmentorhub.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.security.Role;
import com.fcmh.femcodersmentorhub.utils.ApiTestHelper;
import com.fcmh.femcodersmentorhub.utils.UserTestHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserAuthControllerIntegrationTest {

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

    private ApiTestHelper apiHelper;

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String TEST_USERNAME = "Cris Mouta";
    private static final String TEST_EMAIL = "cris.mouta@fcmh.com";
    private static final String TEST_PASSWORD = "Password123.";
    private static final Role TEST_ROLE = Role.MENTOR;

    @BeforeEach
    void setUp() {
        apiHelper = new ApiTestHelper(mockMvc, objectMapper);
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

        apiHelper.performRequest(post(REGISTER_URL), newUser,"User registered successfully")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.role").value(TEST_ROLE.toString()));

        assertTrue(userAuthRepository.existsByEmail(TEST_EMAIL));
        assertTrue(userAuthRepository.existsByUsername(TEST_USERNAME));

        UserAuth saverUser = userAuthRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertNotEquals(TEST_PASSWORD, saverUser.getPassword());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, saverUser.getPassword()));
    }

    @Test
    @DisplayName("POST /login - should login successfully with email")
    void login_WhenValidEmailAndPassword_ReturnsToken() throws Exception {
        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        apiHelper.performRequest(post(LOGIN_URL), loginRequest, "Login successful")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.token").value(notNullValue()));

    }
}
