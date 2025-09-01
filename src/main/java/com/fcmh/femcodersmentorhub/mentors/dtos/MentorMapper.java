package com.fcmh.femcodersmentorhub.mentors.dtos;

import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MentorMapper {
    public static MentorProfile dtoToEntity(MentorRequest dto) {
        return MentorProfile.builder()
                .fullName(dto.fullName().trim())
                .technologies(dto.technologies().stream()
                        .map(String::trim)
                        .collect(Collectors.toList()))
                .level(dto.level())
                .bio(dto.bio().trim())
                .build();
    }

    public static MentorResponse entityToDto(MentorProfile mentorProfile) {
        return new MentorResponse(
                mentorProfile.getFullName(),
                mentorProfile.getTechnologies(),
                mentorProfile.getLevel(),
                mentorProfile.getBio()
        );
    }
}