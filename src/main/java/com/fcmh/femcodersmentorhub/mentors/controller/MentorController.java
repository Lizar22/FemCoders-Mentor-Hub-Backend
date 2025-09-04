package com.fcmh.femcodersmentorhub.mentors.controller;

import com.fcmh.femcodersmentorhub.mentors.Level;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorRequest;
import com.fcmh.femcodersmentorhub.mentors.dtos.MentorResponse;
import com.fcmh.femcodersmentorhub.mentors.services.MentorServiceImpl;
import com.fcmh.femcodersmentorhub.shared.responses.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
@Tag(name = "Mentors", description = "Mentor profile management operations")
public class MentorController {

    private final MentorServiceImpl mentorService;

    @GetMapping
    @Operation(summary = "Get all mentor profiles",
            description = "Retrieve a list of mentors with optional filtering by technologies and experience levels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor profiles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
    })
    @Parameter(name = "technologies", description = "Filter by technology skills",
            example = "Java, Spring Boot, React")
    @Parameter(name = "levels", description = "Filter by experience level")
    public ResponseEntity<SuccessResponse<List<MentorResponse>>> getAllMentors(
            @RequestParam(required = false) List<String> technologies,
            @RequestParam(required = false) List<Level> levels) {

        List<MentorResponse> mentors = mentorService.getAllMentors(technologies, levels);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentor profiles list retrieved successfully", mentors));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mentor profile by ID",
            description = "Retrieve a specific mentor profile by its identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Mentor profile not found")
    })
    @Parameter(description = "ID of the mentor", required = true)
    public ResponseEntity<SuccessResponse<MentorResponse>> getMentorProfileById(@PathVariable Long id) {

        MentorResponse mentorProfile = mentorService.getMentorProfileById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentor profile retrieved successfully", mentorProfile));
    }

    @PostMapping
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Create a mentor profile",
            description = "Allows an authenticated user with MENTOR role to create their mentor profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mentor profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Mentor profile already exists for this user")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<MentorResponse>> addMentorProfile(
            @Valid @RequestBody MentorRequest mentorRequest, Authentication authentication) {

        MentorResponse newMentorProfile = mentorService.addMentorProfile(mentorRequest, authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("Mentor profile created successfully", newMentorProfile));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Update mentor profile",
            description = "Allows an authenticated mentor to update their own profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Mentor profile not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<MentorResponse>> updateMentorProfile(
            @Valid @RequestBody MentorRequest mentorRequest, Authentication authentication) {

        MentorResponse updatedMentorProfile = mentorService.updateMentorProfile(mentorRequest, authentication);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentor profile updated successfully", updatedMentorProfile));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('MENTOR')")
    @Operation(summary = "Delete mentor profile",
            description = "Allows an authenticated mentor to delete their own profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Mentor profile not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<Void>> deleteMentorProfile(Authentication authentication) {

        mentorService.deleteMentorProfile(authentication);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentor profile deleted successfully"));
    }
}