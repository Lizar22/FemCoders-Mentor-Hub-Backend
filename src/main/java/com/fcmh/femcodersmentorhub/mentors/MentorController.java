package com.fcmh.femcodersmentorhub.mentors;

import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;
import com.fcmh.femcodersmentorhub.mentors.services.MentorServiceImpl;
import com.fcmh.femcodersmentorhub.shared.responses.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
public class MentorController {

    private final MentorServiceImpl mentorService;

    public MentorController(MentorServiceImpl mentorService) {
        this.mentorService = mentorService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<MentorResponse>>> getAllMentors(
            @RequestParam(required = false) Level level) {

        List<MentorResponse> mentors = mentorService.getAllMentors(level);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentors list retrieved successfully", mentors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<MentorResponse>> getMentorProfileById(@PathVariable Long id) {
        MentorResponse mentorProfile = mentorService.getMentorProfileById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentor profile retrieved successfully", mentorProfile));
    }

    @PostMapping
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SuccessResponse<MentorResponse>> addMentorProfile(@Valid @RequestBody MentorRequest mentorRequest, Authentication authentication) {
        MentorResponse newMentorProfile = mentorService.addMentorProfile(mentorRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("Mentor profile created successfully", newMentorProfile));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SuccessResponse<MentorResponse>> updateMentorProfile(@Valid @RequestBody MentorRequest mentorRequest, Authentication authentication) {
        MentorResponse updatedMentorProfile = mentorService.updateMentorProfile(mentorRequest, authentication);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentor profile updated successfully", updatedMentorProfile));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SuccessResponse<Void>> deleteMentorProfile(Authentication authentication) {
        mentorService.deleteMentorProfile(authentication);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentor profile deleted successfully"));
    }
}