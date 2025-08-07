package com.fcmh.femcodersmentorhub.auth;

import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;
import com.fcmh.femcodersmentorhub.auth.services.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthController {
    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAuthResponse> addUser(@RequestBody @Valid UserAuthRequest userAuthRequest) {
        return new ResponseEntity<>(userAuthService.addUser(userAuthRequest), HttpStatus.CREATED);
    }
}
