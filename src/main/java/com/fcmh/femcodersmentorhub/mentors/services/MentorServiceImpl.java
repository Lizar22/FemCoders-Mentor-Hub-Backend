package com.fcmh.femcodersmentorhub.mentors.services;

import com.fcmh.femcodersmentorhub.auth.exceptions.UserNotFoundException;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.MentorRepository;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorMapper;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentorServiceImpl implements MentorService {
    private final MentorRepository mentorRepository;

    public MentorServiceImpl(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    @Override
    public List<MentorResponse> getAllMentors() {
        List<MentorProfile> mentors = mentorRepository.findAll();
        return mentors.stream().map(MentorMapper::entityToDto).toList();
    }

    @Override
    public MentorResponse getMentorProfileById(Long id) {
        MentorProfile mentor = mentorRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        return MentorMapper.entityToDto(mentor);
    }

    @Override
    public MentorResponse addMentorProfile(MentorRequest mentorRequest) {
        MentorProfile newMentor = MentorMapper.dtoToEntity(mentorRequest);
        MentorProfile savedMentor = mentorRepository.save(newMentor);
        return MentorMapper.entityToDto(savedMentor);
    }

    @Override
    public MentorResponse updateMentorProfile(Long id, MentorRequest mentorRequest) {
        MentorProfile existingMentor = mentorRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        existingMentor.setFullName(mentorRequest.fullName());
        existingMentor.setTechnologies(mentorRequest.technologies());
        existingMentor.setLevel(mentorRequest.level());
        existingMentor.setBio(mentorRequest.bio());
        MentorProfile updatedMentor = mentorRepository.save(existingMentor);
        return MentorMapper.entityToDto(updatedMentor);
    }

    @Override
    public void deleteMentorProfile(Long id) {
        getMentorProfileById(id);
        mentorRepository.deleteById(id);
    }
}