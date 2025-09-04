package com.fcmh.femcodersmentorhub.utils;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.mentors.Level;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.repository.MentorRepository;
import com.fcmh.femcodersmentorhub.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserTestHelper {

    private final UserAuthRepository userAuthRepository;
    private final MentorRepository mentorRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuth existingUser(String username, String email, String password, Role role) {

        UserAuth user = UserAuth.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        userAuthRepository.save(user);

        return user;
    }

    public MentorProfile existingMentor(String username, String email, String password, String fullName, List<String> technologies, Level level, String bio) {

        UserAuth mentorUser = existingUser(username, email, password, Role.MENTOR);

        MentorProfile mentorProfile = MentorProfile.builder()
                .fullName(fullName)
                .technologies(technologies)
                .level(level)
                .bio(bio)
                .user(mentorUser)
                .build();

        return mentorRepository.save(mentorProfile);
    }

    public record TestUsers(
            Long mentorProfileId, Long anotherMentorProfileId
    ){
    }

    public TestUsers createDefaultTestUsers() {

        existingUser("ana.mentee", "ana.mentee@fcmh.com", "Password123.", Role.MENTEE);

        MentorProfile mentorProfile = existingMentor(
                "cris.mentor",
                "cris.mentor@fcmh.com",
                "Password123.",
                "Cris Mentor",
                List.of("Java", "Spring Boot"),
                Level.SENIOR,
                "Test bio"
        );
        Long mentorProfileId = mentorProfile.getId();

        MentorProfile anotherMentorProfile = existingMentor(
                "other.mentor",
                "other.mentor@fcmh.com",
                "Password123.",
                "Another Mentor",
                List.of("Python", "Django"),
                Level.JUNIOR,
                "Test bio"
        );
        Long anotherMentorProfileId = anotherMentorProfile.getId();

        return new TestUsers(mentorProfileId, anotherMentorProfileId);
    }

    private Long getMentorProfileId(UserAuth mentorUser) {
        return mentorRepository.findByUser(mentorUser).orElseThrow().getId();
    }
}
