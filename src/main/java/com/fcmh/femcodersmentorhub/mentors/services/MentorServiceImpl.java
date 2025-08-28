package com.fcmh.femcodersmentorhub.mentors.services;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.exceptions.UserNotFoundException;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.mentors.Level;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.mentors.MentorRepository;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorMapper;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;
import com.fcmh.femcodersmentorhub.mentors.exceptions.MentorProfileAlreadyExistsException;
import com.fcmh.femcodersmentorhub.mentors.exceptions.MentorProfileNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentorServiceImpl implements MentorService {
    private final MentorRepository mentorRepository;
    private final UserAuthRepository userAuthRepository;

    public MentorServiceImpl(MentorRepository mentorRepository, UserAuthRepository userAuthRepository) {
        this.mentorRepository = mentorRepository;
        this.userAuthRepository = userAuthRepository;
    }

    @Override
    public List<MentorResponse> getAllMentors(List<String> technologies, List<Level> levels) {
        return mentorRepository.findByFilters(technologies, levels)
                .stream()
                .map(MentorMapper::entityToDto)
                .toList();
    }

    @Override
    public MentorResponse getMentorProfileById(Long id) {
        MentorProfile mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        return MentorMapper.entityToDto(mentor);
    }

    @Override
    public MentorResponse addMentorProfile(MentorRequest mentorRequest, Authentication authentication) {
        UserAuth user = getAuthenticatedUser(authentication);

        if (mentorRepository.existsByUser(user)) {
            throw new MentorProfileAlreadyExistsException("A mentor profile already exists for this user");
        }
        MentorProfile newMentor = MentorMapper.dtoToEntity(mentorRequest);
        newMentor.setUser(user);
        MentorProfile savedMentor = mentorRepository.save(newMentor);
        return MentorMapper.entityToDto(savedMentor);
    }

    @Override
    public MentorResponse updateMentorProfile(MentorRequest mentorRequest, Authentication authentication) {
        UserAuth user = getAuthenticatedUser(authentication);

        MentorProfile existingMentor = mentorRepository.findByUser(user)
                .orElseThrow(() -> new MentorProfileNotFoundException("Mentor profile not found"));
        existingMentor.setFullName(mentorRequest.fullName());
        existingMentor.setTechnologies(mentorRequest.technologies());
        existingMentor.setLevel(mentorRequest.level());
        existingMentor.setBio(mentorRequest.bio());

        MentorProfile updatedMentor = mentorRepository.save(existingMentor);
        return MentorMapper.entityToDto(updatedMentor);
    }

    @Override
    public void deleteMentorProfile(Authentication authentication) {
        UserAuth user = getAuthenticatedUser(authentication);

        MentorProfile mentorProfile = mentorRepository.findByUser(user)
                .orElseThrow(() -> new MentorProfileNotFoundException("Mentor profile not found"));

        mentorRepository.delete(mentorProfile);
    }

    private UserAuth getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        return userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}