package com.fcmh.femcodersmentorhub.requests.repository;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.requests.MentoringRequest;
import com.fcmh.femcodersmentorhub.requests.RequestStatus;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentoringRequestRepository extends JpaRepository<MentoringRequest, Long> {
    List<MentoringRequestResponse> findByMentee(UserAuth mentee);
    List<MentoringRequestResponse> findByMentorProfile(MentorProfile mentorProfile);
    boolean existsByMenteeAndMentorProfileAndStatus(UserAuth mentee, MentorProfile mentorProfile, RequestStatus status);
}