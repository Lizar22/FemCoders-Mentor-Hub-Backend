package com.fcmh.femcodersmentorhub.jwt;

import com.fcmh.femcodersmentorhub.security.CustomUserDetails;
import com.fcmh.femcodersmentorhub.security.Role;
import com.fcmh.femcodersmentorhub.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private CustomUserDetails testUserDetails;
    private static final String TEST_USERNAME = "Cris Mouta";
    private static final Role TEST_ROLE = Role.MENTOR;
    private static final String TEST_JWT_SECRET_KEY = "mySecretKeyForTestingPurposesOnly123456789123456789";
    private static final Long TEST_JWT_EXPIRATION = 18000000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", TEST_JWT_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_JWT_EXPIRATION);
    }

    private String generateTestToken() {
        testUserDetails = mock(CustomUserDetails.class);
        when(testUserDetails.getUsername()).thenReturn(TEST_USERNAME);

        doReturn(List.of(new SimpleGrantedAuthority("ROLE_" + TEST_ROLE.name())))
                .when(testUserDetails).getAuthorities();

        return jwtService.generateToken(testUserDetails);
    }

    @Test
    @DisplayName("Generate Token - should generate valid JWT token")
    void generateToken_WhenValidUserDetails_ReturnsValidToken() {
        String token = generateTestToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."), "Token should have JWT format with dots");
    }

    @Test
    @DisplayName("Extract Username - should extract username from valid JWT token")
    void extractUsername_WhenValidToken_ReturnsCorrectUsername() {
        String token = generateTestToken();

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    @DisplayName("Extract Username - should throw exception for invalid token")
    void extractUsername_WhenInvalidToken_ThrowsException() {
        String invalidToken = "lololo";

        assertThrows(Exception.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    @DisplayName("Extract Expiration - should extract expiration date from valid JWT token")
    void extractExpiration_WhenValidToken_ReturnsValidExpirationDate() {
        String token = generateTestToken();
        Date afterGeneration = new Date();
        Date extractedExpiration = jwtService.extractExpiration(token);

        assertNotNull(extractedExpiration);
        assertTrue(extractedExpiration.after(new Date()));
        long expectedExpiration = afterGeneration.getTime() + TEST_JWT_EXPIRATION;
        long actualExpiration = extractedExpiration.getTime();
        assertTrue(Math.abs(actualExpiration - expectedExpiration) < 5000, "Expiration should be 30 minutes from now on");
    }

    @Test
    @DisplayName("Extract Expiration - should throw exception for invalid token")
    void extractExpiration_WhenInvalidToken_ThrowsException() {
        String invalidToken = "lololo";

        assertThrows(Exception.class, () -> jwtService.extractExpiration(invalidToken));
    }

    @Test
    @DisplayName("Is Valid Token - should return true for valid token and matching user")
    void isValidToken_WhenValidTokenAndMatchingUser_ReturnsTrue() {
        String token = generateTestToken();

        boolean isValid = jwtService.isValidToken(token, testUserDetails);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Is Valid Token - should return false for valid token and unmatching user")
    void isValidToken_WhenValidTokenButDifferentUser_ReturnsFalse() {
        String token = generateTestToken();
        CustomUserDetails differentUser = mock(CustomUserDetails.class);

        when(differentUser.getUsername()).thenReturn("lalala");

        boolean isValid = jwtService.isValidToken(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Is Valid Token - should return false for invalid token")
    void isValidToken_WhenInvalidToken_ReturnsFalse() {
        String invalidToken = "lololo";

        boolean isValid = jwtService.isValidToken(invalidToken, testUserDetails);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Is Valid Token - should return false for expired token")
    void isValidToken_WhenExpiredToken_ReturnsFalse() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        String token = generateTestToken();

        Thread.sleep(10);

        boolean isValid = jwtService.isValidToken(token, testUserDetails);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Is Valid Token - should return false for null token")
    void isValidToken_WhenNullToken_ReturnsFalse() {
        boolean isValid = jwtService.isValidToken(null, testUserDetails);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Is Valid Token - should return false for empty token")
    void isValidToken_WhenEmptyToken_ReturnsFalse() {
        boolean isValid = jwtService.isValidToken("", testUserDetails);

        assertFalse(isValid);
    }
}
