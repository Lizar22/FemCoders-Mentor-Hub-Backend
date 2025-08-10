package com.fcmh.femcodersmentorhub;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthMapper;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthResponse;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.auth.services.UserAuthServiceImpl;
import com.fcmh.femcodersmentorhub.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAuthServiceImplTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAuthServiceImpl userAuthService;

    private UserAuth testUserAuth;
    private UserAuthRequest testUserAuthRequest;
    private UserAuthResponse testUserAuthResponse;
    private final Long TEST_USER_ID = 1L;
    private final String TEST_USERNAME = "Cris Mouta";
    private final String TEST_EMAIL = "cris.mouta@fcmh.com";
    private final String TEST_PASSWORD = "Password123.";
    private final String ENCODED_PASSWORD = "$2a$10$EncodedPasswordHash";
    private final Role TEST_ROLE = Role.MENTOR;

    @BeforeEach
    void setUp() {
        testUserAuth = new UserAuth();
        testUserAuth.setId(TEST_USER_ID);
        testUserAuth.setUsername(TEST_USERNAME);
        testUserAuth.setEmail(TEST_EMAIL);
        testUserAuth.setPassword(ENCODED_PASSWORD);
        testUserAuth.setRole(TEST_ROLE);

        testUserAuthRequest = new UserAuthRequest(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);
        testUserAuthResponse = new UserAuthResponse(TEST_USERNAME, TEST_EMAIL, TEST_ROLE);
    }

    @Test
    @DisplayName("POST /register - should add a new user successfully")
    void addUser_WhenValidData_ReturnsOkAndCreatedUser() {
        when(userAuthRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userAuthRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userAuthRepository.save(any(UserAuth.class))).thenReturn(testUserAuth);

        UserAuthResponse result = userAuthService.addUser(testUserAuthRequest);

        assertNotNull(result);
        verify(userAuthRepository).existsByEmail(TEST_EMAIL);
        verify(userAuthRepository).existsByUsername(TEST_USERNAME);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userAuthRepository).save(any(UserAuth.class));
    }


}
