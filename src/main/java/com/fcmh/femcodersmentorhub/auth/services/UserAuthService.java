package com.fcmh.femcodersmentorhub.auth.services;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.UserAuthRepository;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthMapper;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {
    private final UserAuthRepository userAuthRepository;

    public UserAuthService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    public UserAuthResponse addUser(UserAuthRequest userAuthRequest) {
        UserAuth newUser = UserAuthMapper.dtoToEntity(userAuthRequest);
        UserAuth savedUser = userAuthRepository.save(newUser);
        return UserAuthMapper.entityToDto(savedUser);
    }
}
