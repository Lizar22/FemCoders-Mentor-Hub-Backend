package com.fcmh.femcodersmentorhub.auth.services;

import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;

public interface UserAuthService {
    UserAuthResponse findUserById(Long id);
    UserAuthResponse addUser(UserAuthRequest userRequest);
}
