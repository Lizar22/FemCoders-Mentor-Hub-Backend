package com.fcmh.femcodersmentorhub;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginResponse;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthResponse;
import com.fcmh.femcodersmentorhub.auth.exceptions.InvalidCredentialsException;
import com.fcmh.femcodersmentorhub.auth.exceptions.UserAlreadyExistsException;
import com.fcmh.femcodersmentorhub.auth.exceptions.UserNotFoundException;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.auth.services.UserAuthServiceImpl;
import com.fcmh.femcodersmentorhub.security.CustomUserDetails;
import com.fcmh.femcodersmentorhub.security.Role;
import com.fcmh.femcodersmentorhub.security.jwt.JwtService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAuthServiceImplTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserAuthServiceImpl userAuthService;
    private static Validator validator;

    private UserAuth testUserAuth;
    private UserAuthRequest testUserAuthRequest;
    private UserAuthResponse testUserAuthResponse;
    private LoginRequest testLoginRequest;
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "Cris Mouta";
    private static final String TEST_EMAIL = "cris.mouta@fcmh.com";
    private static final String TEST_PASSWORD = "Password123.";
    private static final String ENCODED_PASSWORD = "$2a$10$EncodedPasswordHash";
    private static final String TEST_JWT_TOKEN = "jwt.token.example";
    private static final Role TEST_ROLE = Role.MENTOR;
    private static final String TEST_IDENTIFIER_EMAIL = "cris.mouta@fcmh.com";
    private static final String TEST_IDENTIFIER_USERNAME = "Cris Mouta";

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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
        testLoginRequest = new LoginRequest(TEST_IDENTIFIER_EMAIL, TEST_PASSWORD);
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

    @Test
    @DisplayName("POST /register - should return an user already exists error 409")
    void addUser_WhenEmailAlreadyExists_ReturnsUserAlreadyExistsError() {
        when(userAuthRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userAuthService.addUser(testUserAuthRequest)
        );

        assertTrue(exception.getMessage().contains("email"));
        verify(userAuthRepository).existsByEmail(TEST_EMAIL);
        verify(userAuthRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST /register - should return an user already exists error 409")
    void addUser_WhenUsernameAlreadyExists_ReturnsUserAlreadyExistsError() {
        when(userAuthRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userAuthRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userAuthService.addUser(testUserAuthRequest)
        );

        assertTrue(exception.getMessage().contains("username"));
        verify(userAuthRepository).existsByUsername(TEST_USERNAME);
        verify(userAuthRepository, never()).save(any());
    }

    static Stream<Arguments> invalidUserAuthDtos() {
        return Stream.of(
                Arguments.of(new UserAuthRequest("", TEST_EMAIL, TEST_PASSWORD, TEST_ROLE),
                        "Username is required"),
                Arguments.of(new UserAuthRequest("M",TEST_EMAIL, TEST_PASSWORD, TEST_ROLE ),
                        "Username must contain between 2 and 50 characters"),
                Arguments.of(new UserAuthRequest(TEST_USERNAME, "kkk", TEST_PASSWORD, TEST_ROLE),
                        "Invalid e-mail format"),
                Arguments.of(new UserAuthRequest(TEST_USERNAME, TEST_EMAIL, "ggg", TEST_ROLE),
                        "Password must contain a minimum of 12 characters, including a number, one uppercase letter, one lowercase letter and one special character"),
                Arguments.of(new UserAuthRequest(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null),
                        "Role is required. You must choose between MENTOR or MENTEE")
        );
    }

    @ParameterizedTest(name = "{index} -> {1}")
    @MethodSource("invalidUserAuthDtos")
    void whenInvalidUserAuthDto_ReturnsValidationError(UserAuthRequest dto, String expectedMessage) {
        Set<ConstraintViolation<UserAuthRequest>> violations = validator.validate(dto);

        assertTrue(violations
                        .stream().anyMatch(violation -> violation.getMessage().contains(expectedMessage)),
                () -> "Expected violation containing: " + expectedMessage);
    }

    @ParameterizedTest
    @DisplayName("POST /login - should login a user successfully")
    @ValueSource(strings = {TEST_IDENTIFIER_EMAIL, TEST_IDENTIFIER_USERNAME})
    void login_WhenValidIdentifier_ReturnsToken(String identifier) {
        Authentication mockAuthentication = mock(Authentication.class);
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        LoginRequest loginRequest = new LoginRequest(identifier, TEST_PASSWORD);

        when(authenticationManager.authenticate(any()))
                .thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn(TEST_JWT_TOKEN);

        LoginResponse result = userAuthService.login(loginRequest);

        assertNotNull(result);
        assertEquals(TEST_JWT_TOKEN, result.token());
        assertNotNull(result.token(), "Token should not be null");
        assertFalse(result.token().isBlank(), "Token should not be empty");
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(mockUserDetails);
    }

    @Test
    @DisplayName("POST /login - should return user not found error 404")
    void login_WhenUserNotFound_ThrowsUserNotFoundException(){
        LoginRequest loginRequest = new LoginRequest(TEST_IDENTIFIER_USERNAME, TEST_PASSWORD);

        when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("User not found"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userAuthService.login(loginRequest));

        assertTrue(exception.getMessage().contains("User not found: " + loginRequest.identifier()));
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("POST /login - should return invalid credentials error 401")
    void login_WhenInvalidCredentials_ThrowsInvalidCredentialsException(){
        LoginRequest loginRequest = new LoginRequest(TEST_IDENTIFIER_EMAIL, TEST_PASSWORD);

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> userAuthService.login(loginRequest));

        assertTrue(exception.getMessage().contains("Invalid credentials for: " + loginRequest.identifier()));
        verify(authenticationManager).authenticate(any());
    }

    static Stream<Arguments> invalidLoginDtos() {
        return Stream.of(
                Arguments.of(new LoginRequest("", TEST_PASSWORD),
                        "Username or e-mail is required"),
                Arguments.of(new LoginRequest(null, TEST_PASSWORD),
                        "Username or e-mail is required"),
                Arguments.of(new LoginRequest(TEST_IDENTIFIER_USERNAME, ""),
                        "Password is required"),
                Arguments.of(new LoginRequest(TEST_IDENTIFIER_EMAIL, null),
                        "Password is required")
        );
    }

    @ParameterizedTest(name = "{index} -> {1}")
    @MethodSource("invalidLoginDtos")
    void whenInvalidLoginDto_ReturnsValidationError(LoginRequest dto, String expectedMessage) {
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(dto);

        assertTrue(violations
                        .stream().anyMatch(violation -> violation.getMessage().contains(expectedMessage)),
                () -> "Expected violation containing: " + expectedMessage);
    }
}
