package com.fcmh.femcodersmentorhub.requests.repository;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.requests.MentoringRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentoringRequestRepository extends JpaRepository<MentoringRequest, Long> {
    List<MentoringRequest> findByMentee(UserAuth mentee);
    List<MentoringRequest> findByMentorProfile(MentorProfile mentorProfile);
}
