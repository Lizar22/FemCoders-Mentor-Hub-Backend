package com.fcmh.femcodersmentorhub.mentors;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentorRepository extends JpaRepository <MentorProfile, Long> {
    boolean existsByUser(UserAuth user);
    Optional<MentorProfile> findByUser(UserAuth user);

    @Query("SELECT m FROM MentorProfile m WHERE (:level IS NULL OR m.level = :level)")
    List<MentorProfile> findByLevel(@Param("level") Level level);
}
