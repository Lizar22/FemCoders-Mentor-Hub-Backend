package com.fcmh.femcodersmentorhub.auth.services;

import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;

import java.util.List;

public interface UserAuthService {
    UserAuthResponse findUserById(Long id);
    UserAuthResponse addUser(UserAuthRequest userRequest);
    UserAuthResponse authenticateUser(String email, String password);
    /*List<UserAuthResponse> findAllUsers();
    UserAuthResponse updateUser(Long id, UserAuthRequest userRequest);
    void deleteUser(Long id);*/
}
