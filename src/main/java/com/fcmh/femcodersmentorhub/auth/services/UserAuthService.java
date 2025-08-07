package com.fcmh.femcodersmentorhub.auth.services;

import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;

import java.util.List;

public interface UserAuthService {
    List<UserAuthResponse> findAllUsers();
    UserAuthResponse findUserById(Long id);
    UserAuthResponse addUser(UserAuthRequest userRequest);
    UserAuthResponse updateUser(Long id, UserAuthRequest userRequest);
    void deleteUser(Long id);


}
