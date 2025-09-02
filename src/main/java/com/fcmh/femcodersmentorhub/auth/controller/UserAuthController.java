package com.fcmh.femcodersmentorhub.auth.controller;

import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginResponse;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthResponse;
import com.fcmh.femcodersmentorhub.auth.services.UserAuthServiceImpl;
import com.fcmh.femcodersmentorhub.shared.responses.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class UserAuthController {

    private final UserAuthServiceImpl userAuthServiceImpl;

    @PostMapping("/register")
    @Operation(summary = "Register new user",
            description = "Creates a new account for a mentor or mentee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Email or username already exists")
    })
    public ResponseEntity<SuccessResponse<UserAuthResponse>> registerUser(@RequestBody @Valid UserAuthRequest userAuthRequest) {

        UserAuthResponse userAuthResponse = userAuthServiceImpl.addUser(userAuthRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("User registered successfully", userAuthResponse)
        );
    }

    @PostMapping("/login")
    @Operation(summary = "User login",
            description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<SuccessResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {

        LoginResponse loginResponse = userAuthServiceImpl.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Login successful", loginResponse)
        );
    }
}