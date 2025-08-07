package com.fcmh.femcodersmentorhub.auth.controller;

import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;
import com.fcmh.femcodersmentorhub.auth.services.UserAuthServiceImpl;
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
    public ResponseEntity<UserAuthResponse> registerUser(@RequestBody @Valid UserAuthRequest userAuthRequest) {
        UserAuthResponse userAuthResponse = userAuthServiceImpl.addUser(userAuthRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAuthResponse);
    }
}
