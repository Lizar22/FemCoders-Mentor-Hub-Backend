package com.fcmh.femcodersmentorhub.requests.controller;

import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMenteeRequest;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMentorUpdatedResponse;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestResponse;
import com.fcmh.femcodersmentorhub.requests.services.MentoringRequestServiceImpl;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentoring-requests")
@RequiredArgsConstructor
@Tag(name = "Mentoring Requests", description = "Operations related to mentoring requests")
@SecurityRequirement(name = "bearerAuth")
public class MentoringRequestController {
    private final MentoringRequestServiceImpl mentoringRequestService;

    @GetMapping
    @Operation(summary = "Get my mentoring requests",
            description = "Retrieve a list of mentoring requests for the authenticated user (mentee or mentor)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentoring requests retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: invalid or missing JWT")
    })
    public ResponseEntity<SuccessResponse<List<MentoringRequestResponse>>> getMyMentoringRequests(Authentication authentication) {

        List<MentoringRequestResponse> requests = mentoringRequestService.getMyMentoringRequests(authentication);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentoring requests list retrieved successfully", requests));
    }

    @PostMapping
    @Operation(summary = "Create a mentoring request",
            description = "Allows an authenticated mentee to create a new mentoring request to a mentor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mentoring request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or existing pending request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: invalid or missing JWT"),
            @ApiResponse(responseCode = "404", description = "Mentor profile not found")
    })
    @Parameter(description = "Mentoring request data", required = true)
    public ResponseEntity<SuccessResponse<MentoringRequestResponse>> createMentoringRequest(
            @Valid @RequestBody MentoringRequestMenteeRequest request, Authentication authentication) {

        MentoringRequestResponse newRequest = mentoringRequestService.addMentoringRequest(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("Mentoring request created successfully", newRequest));
    }

    @PutMapping("/{id}/respond")
    @Operation(summary = "Respond to a mentoring request",
            description = "Allows an authenticated mentor to respond to a pending mentoring request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentoring request updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or request already answered"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: invalid or missing JWT or not a mentor"),
            @ApiResponse(responseCode = "403", description = "Forbidden: mentor not assigned to this request"),
            @ApiResponse(responseCode = "404", description = "Mentoring request not found")
    })
    @Parameter(description = "ID of the mentoring request", required = true)
    @Parameter(description = "Mentor's response data", required = true)
    public ResponseEntity<SuccessResponse<MentoringRequestResponse>> respondToRequest(
            @PathVariable Long id,
            @Valid @RequestBody MentoringRequestMentorUpdatedResponse mentorUpdatedResponse, Authentication authentication) {

        MentoringRequestResponse updatedRequest = mentoringRequestService.respondToRequest(id, mentorUpdatedResponse, authentication);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentoring request updated successfully", updatedRequest));
    }
}