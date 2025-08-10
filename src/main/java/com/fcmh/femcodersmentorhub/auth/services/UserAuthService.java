package com.fcmh.femcodersmentorhub.auth.services;

import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginResponse;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthResponse;

import javax.security.auth.login.LoginContext;

public interface UserAuthService {
    UserAuthResponse findUserById(Long id);
    UserAuthResponse addUser(UserAuthRequest userRequest);
    LoginResponse login(LoginRequest loginRequest);
}
