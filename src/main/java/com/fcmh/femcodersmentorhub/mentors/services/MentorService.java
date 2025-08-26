package com.fcmh.femcodersmentorhub.mentors.services;

import com.fcmh.femcodersmentorhub.mentors.MentorProfile;

public interface MentorService {
    MentorProfile getAllMentors();
    MentorProfile getMentorById(Long id);
}
