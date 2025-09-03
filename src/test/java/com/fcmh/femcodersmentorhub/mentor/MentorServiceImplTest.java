package com.fcmh.femcodersmentorhub.mentor;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.exceptions.UserNotFoundException;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.mentors.Level;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.repository.MentorRepository;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;
import com.fcmh.femcodersmentorhub.mentors.exceptions.MentorProfileAlreadyExistsException;
import com.fcmh.femcodersmentorhub.mentors.exceptions.MentorProfileNotFoundException;
import com.fcmh.femcodersmentorhub.mentors.services.MentorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorServiceImplTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_FULL_NAME = "Cris Mouta";
    private static final List<String> TEST_TECHNOLOGIES = List.of("Java", "Spring Boot", "React");
    private static final Level TEST_LEVEL = Level.SENIOR;
    private static final String TEST_BIO = "A passion for teaching";
    private static final Long INVALID_ID = 999L;
    private static final String TEST_USERNAME = "cris.mouta@test.com";
    private static final String NON_EXISTENT_USERNAME = "nonexistentusername@test.com";

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MentorServiceImpl mentorServiceImpl;

    private MentorProfile testMentorProfile;
    private MentorRequest testMentorRequest;
    private UserAuth testUser;

    @BeforeEach
    void setUp() {

        testUser = createTestUser();
        testMentorProfile = createTestMentorProfile();
        testMentorRequest = createTestMentorRequest();
    }

    @Test
    @DisplayName("GET /mentors - should list all mentors successfully")
    void getAllMentors_WhenValidData_ReturnsOkAndListOfMentors() {

        List<String> technologies = List.of("Java", "Spring Boot");
        List<Level> levels = List.of(Level.SENIOR);
        List<MentorProfile> mockProfiles = List.of(testMentorProfile);

        when(mentorRepository.findByFilters(technologies, levels)).thenReturn(mockProfiles);

        List<MentorResponse> result = mentorServiceImpl.getAllMentors(technologies, levels);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().fullName()).isEqualTo(TEST_FULL_NAME);
        assertThat(result.getFirst().level()).isEqualTo(TEST_LEVEL);
        assertThat(result.getLast().technologies()).isEqualTo(TEST_TECHNOLOGIES);

        verify(mentorRepository).findByFilters(technologies, levels);
    }

    @Test
    @DisplayName("GET /mentors - should return empty list when no mentors found")
    void getAllMentors_WhenNoMentorsFoud_ReturnsEmptyList() {

        List<String> technologies = List.of("Python");
        List<Level> levels = List.of(Level.JUNIOR);

        when(mentorRepository.findByFilters(technologies, levels)).thenReturn(Collections.emptyList());

        List<MentorResponse> result = mentorServiceImpl.getAllMentors(technologies, levels);

        assertThat(result).isEmpty();

        verify(mentorRepository).findByFilters(technologies, levels);
    }

    @Test
    @DisplayName("GET /mentors - should handle null filters")
    void getAllMentors_WhenNullFilters_CallsRepositoryWithNullValues() {

        when(mentorRepository.findByFilters(null, null)).thenReturn(List.of(testMentorProfile));

        List<MentorResponse> result = mentorServiceImpl.getAllMentors(null, null);

        assertThat(result).hasSize(1);

        verify(mentorRepository).findByFilters(null, null);
    }

    @Test
    @DisplayName("GET /mentors - should handle mixed null filters")
    void getAllMentors_WhenMixedNullFilters_CallsRepositoryCorrectly() {

        when(mentorRepository.findByFilters(TEST_TECHNOLOGIES, null)).thenReturn(List.of(testMentorProfile));

        List<MentorResponse> result = mentorServiceImpl.getAllMentors(TEST_TECHNOLOGIES, null);

        assertThat(result).hasSize(1);

        verify(mentorRepository).findByFilters(TEST_TECHNOLOGIES, null);
    }

    @Test
    @DisplayName("GET /mentors/{id} - should return mentor profile when valid ID provided")
    void getMentorProfileById_WhenValidId_ReturnsMentorResponse() {

        when(mentorRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testMentorProfile));

        MentorResponse result = mentorServiceImpl.getMentorProfileById(TEST_USER_ID);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo(TEST_FULL_NAME);
        assertThat(result.technologies()).isEqualTo(TEST_TECHNOLOGIES);
        assertThat(result.level()).isEqualTo(TEST_LEVEL);
        assertThat(result.bio()).isEqualTo(TEST_BIO);

        verify(mentorRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("GET /mentors/{id} - should return mentor not found error 404 when invalid ID provided")
    void getMentorProfileById_WhenInvalidId_ThrowsUserNotFoundException() {

        when(mentorRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentorServiceImpl.getMentorProfileById(INVALID_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id " + INVALID_ID);

        verify(mentorRepository).findById(INVALID_ID);
    }

    @Test
    @DisplayName("POST /mentors - should create mentor profile when user has no existing profile")
    void addMentorProfile_WhenNoExistingProfile_ReturnsOkAndCreatesMentorProfile() {

        mockAuthenticatedUser();

        when(mentorRepository.existsByUser(testUser)).thenReturn(false);
        when(mentorRepository.save(any(MentorProfile.class))).thenReturn(testMentorProfile);

        MentorResponse result = mentorServiceImpl.addMentorProfile(testMentorRequest, authentication);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo(TEST_FULL_NAME);

        verify(mentorRepository).existsByUser(testUser);
        verify(mentorRepository).save(any(MentorProfile.class));
    }

    @Test
    @DisplayName("POST /mentors - should return mentor profile already exists error 409")
    void addMentorProfile_WhenProfileAlreadyExists_ThrowsException() {

        mockAuthenticatedUser();

        when(mentorRepository.existsByUser(testUser)).thenReturn(true);

        assertThatThrownBy(() -> mentorServiceImpl.addMentorProfile(testMentorRequest, authentication))
                .isInstanceOf(MentorProfileAlreadyExistsException.class)
                .hasMessage("A mentor profile already exists for this user");

        verify(mentorRepository).existsByUser(testUser);
        verify(mentorRepository, never()).save(any(MentorProfile.class));
    }

    @Test
    @DisplayName("PUT /mentors/me - should update mentor profile when exists")
    void updateMentorProfile_WhenProfileExists_UpdatesSuccessfully() {

        mockAuthenticatedUser();

        when(mentorRepository.findByUser(testUser)).thenReturn(Optional.of(testMentorProfile));
        when(mentorRepository.save(testMentorProfile)).thenReturn(testMentorProfile);

        MentorResponse result = mentorServiceImpl.updateMentorProfile(testMentorRequest, authentication);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo(TEST_FULL_NAME);

        verify(mentorRepository).findByUser(testUser);
        verify(mentorRepository).save(testMentorProfile);

        assertThat(testMentorProfile.getFullName()).isEqualTo(testMentorRequest.fullName());
        assertThat(testMentorProfile.getTechnologies()).isEqualTo(testMentorRequest.technologies());
        assertThat(testMentorProfile.getLevel()).isEqualTo(testMentorRequest.level());
        assertThat(testMentorProfile.getBio()).isEqualTo(testMentorRequest.bio());
    }

    @Test
    @DisplayName("PUT /mentors/me - should return mentor profile not found error 404")
    void updateMentorProfile_WhenProfileNotFound_ThrowsException() {

        mockAuthenticatedUser();

        when(mentorRepository.findByUser(testUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentorServiceImpl.updateMentorProfile(testMentorRequest, authentication))
                .isInstanceOf(MentorProfileNotFoundException.class)
                .hasMessage("Mentor profile not found");

        verify(mentorRepository).findByUser(testUser);
        verify(mentorRepository, never()).save(any(MentorProfile.class));
    }

    @Test
    @DisplayName("DELETE /mentors/me - should delete mentor profile when exists")
    void deleteMentorProfile_WhenProfileExists_DeletesSuccessfully() {

        mockAuthenticatedUser();

        when(mentorRepository.findByUser(testUser)).thenReturn(Optional.of(testMentorProfile));

        mentorServiceImpl.deleteMentorProfile(authentication);

        verify(mentorRepository).findByUser(testUser);
        verify(mentorRepository).delete(testMentorProfile);
    }

    @Test
    @DisplayName("DELETE /mentors/me - should return mentor profile not found error 404")
    void deleteMentorProfile_WhenProfileNotFound_ThrowsException() {

        mockAuthenticatedUser();

        when(mentorRepository.findByUser(testUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentorServiceImpl.deleteMentorProfile(authentication))
                .isInstanceOf(MentorProfileNotFoundException.class)
                .hasMessage("Mentor profile not found");

        verify(mentorRepository).findByUser(testUser);
        verify(mentorRepository, never()).delete(any(MentorProfile.class));
    }

    @Test
    @DisplayName("Should return user not found error in authentication")
    void authenticatedUser_WhenUserNotFound_ThrowsUsernameNotFoundException() {

        when(authentication.getName()).thenReturn(NON_EXISTENT_USERNAME);
        when(userAuthRepository.findByUsername(NON_EXISTENT_USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentorServiceImpl.addMentorProfile(testMentorRequest, authentication))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: " + NON_EXISTENT_USERNAME);

        verify(userAuthRepository).findByUsername(NON_EXISTENT_USERNAME);
    }

    private UserAuth createTestUser() {

        UserAuth user = new UserAuth();
        user.setUsername(TEST_USERNAME);

        return user;
    }

    private MentorProfile createTestMentorProfile() {

        MentorProfile mentor = new MentorProfile();
        mentor.setId(TEST_USER_ID);
        mentor.setUser(testUser);
        mentor.setFullName(TEST_FULL_NAME);
        mentor.setTechnologies(TEST_TECHNOLOGIES);
        mentor.setLevel(TEST_LEVEL);
        mentor.setBio(TEST_BIO);

        return mentor;
    }

    private MentorRequest createTestMentorRequest() {

        return new MentorRequest(TEST_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO);
    }

    private void mockAuthenticatedUser() {

        when(authentication.getName()).thenReturn(TEST_USERNAME);
        when(userAuthRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
    }
}