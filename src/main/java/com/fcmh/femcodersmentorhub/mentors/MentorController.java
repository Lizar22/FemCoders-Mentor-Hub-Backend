package com.fcmh.femcodersmentorhub.mentors;

import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;
import com.fcmh.femcodersmentorhub.mentors.services.MentorServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MentorResponse>> getAllMentors() {
        List<MentorResponse> mentors = mentorService.getAllMentors();
        return new ResponseEntity<>(mentors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorResponse> getMentorProfileById(@PathVariable Long id) {
        MentorResponse mentorProfile = mentorService.getMentorProfileById(id);
        return new ResponseEntity<>(mentorProfile, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<MentorResponse> addMentorProfile(@Valid @RequestBody MentorRequest mentorRequest) {
        MentorResponse newMentorProfile = mentorService.addMentorProfile(mentorRequest);
        return new ResponseEntity<>(newMentorProfile, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MentorResponse> updateMentorProfile(@PathVariable Long id, @Valid @RequestBody MentorRequest mentorRequest) {
        MentorResponse updatedMentorProfile = mentorService.updateMentorProfile(id, mentorRequest);
        return new ResponseEntity<>(updatedMentorProfile, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMentorProfile(@PathVariable Long id) {
        mentorService.deleteMentorProfile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}