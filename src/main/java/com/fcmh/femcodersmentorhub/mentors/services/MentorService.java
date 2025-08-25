package com.fcmh.femcodersmentorhub.mentors.services;

import com.fcmh.femcodersmentorhub.mentors.MentorProfile;

public interface MentorService {
    MentorProfile findAllMentors();
    MentorProfile findMentorById(Long id);
}
