package com.fcmh.femcodersmentorhub.mentors.services;

import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;

import java.util.List;

public interface MentorService {
    List<MentorResponse> getAllMentors();
    MentorResponse getMentorProfileById(Long id);
    MentorResponse addMentorProfile(MentorRequest mentorRequest);
    MentorResponse updateMentorProfile(Long id, MentorRequest mentorRequest);
    void deleteMentorProfile(Long id);
}
