package com.fcmh.femcodersmentorhub.auth.services;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.UserAuthRepository;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthMapper;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {
    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserAuthService(UserAuthRepository userAuthRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userAuthRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAuthResponse addUser(UserAuthRequest userAuthRequest) {
        UserAuth newUser = UserAuthMapper.dtoToEntity(userAuthRequest);
        newUser.setPassword(passwordEncoder.encode(userAuthRequest.password()));
        UserAuth savedUser = userAuthRepository.save(newUser);
        return UserAuthMapper.entityToDto(savedUser);
    }
}
