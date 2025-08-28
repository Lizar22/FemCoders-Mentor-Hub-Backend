package com.fcmh.femcodersmentorhub.mentors.services;

import com.fcmh.femcodersmentorhub.mentors.Level;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MentorService {
    List<MentorResponse> getAllMentors(Level level);
    MentorResponse getMentorProfileById(Long id);
    MentorResponse addMentorProfile(MentorRequest mentorRequest, Authentication authentication);
    MentorResponse updateMentorProfile(MentorRequest mentorRequest, Authentication authentication);
    void deleteMentorProfile(Authentication authentication);
}