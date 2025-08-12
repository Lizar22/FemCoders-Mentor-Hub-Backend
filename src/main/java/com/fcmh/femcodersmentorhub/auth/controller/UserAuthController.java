package com.fcmh.femcodersmentorhub.auth.controller;

import com.fcmh.femcodersmentorhub.auth.dtos.JwtResponse;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;
import com.fcmh.femcodersmentorhub.auth.services.UserAuthServiceImpl;
import com.fcmh.femcodersmentorhub.security.CustomUserDetails;
import com.fcmh.femcodersmentorhub.security.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {
    private final UserAuthServiceImpl userAuthServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserAuthController(UserAuthServiceImpl userAuthServiceImpl, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userAuthServiceImpl = userAuthServiceImpl;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAuthResponse> registerUser(@RequestBody @Valid UserAuthRequest userAuthRequest) {
        UserAuthResponse userAuthResponse = userAuthServiceImpl.addUser(userAuthRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAuthResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody UserAuthRequest userRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.username(), userRequest.password()));

        CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetail);

        JwtResponse jwtResponse = new JwtResponse(token);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }
}