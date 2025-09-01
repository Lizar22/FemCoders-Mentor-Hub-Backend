package com.fcmh.femcodersmentorhub.requests.controller;

import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMenteeRequest;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestMentorUpdatedResponse;
import com.fcmh.femcodersmentorhub.requests.dtos.MentoringRequestResponse;
import com.fcmh.femcodersmentorhub.requests.services.MentoringRequestServiceImpl;
import com.fcmh.femcodersmentorhub.shared.responses.SuccessResponse;
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
public class MentoringRequestController {
    private final MentoringRequestServiceImpl mentoringRequestService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<MentoringRequestResponse>>> getMyMentoringRequests(Authentication authentication) {
        List<MentoringRequestResponse> requests = mentoringRequestService.getMyMentoringRequests(authentication);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentoring requests list retrieved successfully", requests));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<MentoringRequestResponse>> createMentoringRequest(@Valid @RequestBody MentoringRequestMenteeRequest request, Authentication authentication) {
        MentoringRequestResponse newRequest = mentoringRequestService.addMentoringRequest(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("Mentoring request created successfully", newRequest));
    }

    @PutMapping("/{id}/respond")
    public ResponseEntity<SuccessResponse<MentoringRequestResponse>> respondToRequest(@PathVariable Long id, @Valid @RequestBody MentoringRequestMentorUpdatedResponse mentorUpdatedResponse, Authentication authentication) {
        MentoringRequestResponse updatedRequest = mentoringRequestService.respondToRequest(id, mentorUpdatedResponse, authentication);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Mentoring request updated successfully", updatedRequest));
    }
}