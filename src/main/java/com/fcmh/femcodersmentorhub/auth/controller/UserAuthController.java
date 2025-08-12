package com.fcmh.femcodersmentorhub.auth.controller;

import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginResponse;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthResponse;
import com.fcmh.femcodersmentorhub.auth.services.UserAuthServiceImpl;
import com.fcmh.femcodersmentorhub.shared.responses.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthServiceImpl userAuthServiceImpl;
    
    public UserAuthController(UserAuthServiceImpl userAuthServiceImpl) {
        this.userAuthServiceImpl = userAuthServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<UserAuthResponse>> registerUser(@RequestBody @Valid UserAuthRequest userAuthRequest) {

        UserAuthResponse userAuthResponse = userAuthServiceImpl.addUser(userAuthRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("User registered successfully", userAuthResponse)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {

        LoginResponse loginResponse = userAuthServiceImpl.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Login successful", loginResponse)
        );
    }
}