package com.fcmh.femcodersmentorhub.mentors;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository <MentorProfile, Long> {
    boolean existsByUser(UserAuth user);
    Optional<MentorProfile> findByUser(UserAuth user);
}
