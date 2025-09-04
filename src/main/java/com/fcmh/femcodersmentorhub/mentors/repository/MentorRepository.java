package com.fcmh.femcodersmentorhub.mentors.repository;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.mentors.Level;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentorRepository extends JpaRepository <MentorProfile, Long> {

    boolean existsByUser(UserAuth user);

    Optional<MentorProfile> findByUser(UserAuth user);

    @Query("SELECT DISTINCT m FROM MentorProfile m JOIN m.technologies t WHERE " +
            "(:technologies IS NULL OR t IN :technologies) AND " +
            "(:levels IS NULL OR m.level IN :levels)")
    List<MentorProfile> findByFilters(@Param("technologies") List<String> technologies,
                                      @Param("levels") List<Level> levels);
}