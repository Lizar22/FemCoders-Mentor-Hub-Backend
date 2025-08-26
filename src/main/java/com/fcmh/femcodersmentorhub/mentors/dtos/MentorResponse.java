package com.fcmh.femcodersmentorhub.mentors.dtos;

import com.fcmh.femcodersmentorhub.mentors.Level;

import java.util.List;

public record MentorResponse(
        String fullName,
        List<String> technologies,
        Level level,
        String bio
) {
}
