package com.fcmh.femcodersmentorhub.mentor;

import com.fcmh.femcodersmentorhub.mentors.Level;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.MentorRepository;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;
import com.fcmh.femcodersmentorhub.mentors.services.MentorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorServiceImplTest {
    @Mock
    private MentorRepository mentorRepository;

    @InjectMocks
    private MentorServiceImpl mentorServiceImpl;

    private MentorProfile testMentorProfile;
    private MentorRequest testMentorRequest;
    private MentorResponse testMentorResponse;
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_FULL_NAME = "Cris Mouta";
    private static final List<String> TEST_TECHNOLOGIES = List.of("Java", "Spring Boot", "React");
    private static final Level TEST_LEVEL = Level.SENIOR;
    private static final String TEST_BIO = "A passion for teaching";

    @BeforeEach
    void setUp() {
        testMentorProfile = new MentorProfile();
        testMentorProfile.setId(TEST_USER_ID);
        testMentorProfile.setFullName(TEST_FULL_NAME);
        testMentorProfile.setTechnologies(TEST_TECHNOLOGIES);
        testMentorProfile.setLevel(TEST_LEVEL);
        testMentorProfile.setBio(TEST_BIO);

        testMentorRequest = new MentorRequest(TEST_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO);
        testMentorResponse = new MentorResponse(TEST_FULL_NAME, TEST_TECHNOLOGIES, TEST_LEVEL, TEST_BIO);
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
}
