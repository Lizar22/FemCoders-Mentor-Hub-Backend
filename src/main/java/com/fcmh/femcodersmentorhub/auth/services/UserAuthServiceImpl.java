package com.fcmh.femcodersmentorhub.auth.services;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthMapper;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.UserAuthResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService {
    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserAuthMapper userAuthMapper;

    public UserAuthServiceImpl(UserAuthRepository userAuthRepository, BCryptPasswordEncoder passwordEncoder, UserAuthMapper userAuthMapper) {
        this.userAuthRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAuthMapper = userAuthMapper;
    }

        @Override
    public UserAuthResponse findUserById(Long id) {
        return null;
    }

    @Override
    public UserAuthResponse addUser(UserAuthRequest userAuthRequest) {
        UserAuth newUser = userAuthMapper.dtoToEntity(userAuthRequest);
        newUser.setPassword(passwordEncoder.encode(userAuthRequest.password()));
        UserAuth savedUser = userAuthRepository.save(newUser);
        return userAuthMapper.entityToDto(savedUser);
    }

    @Override
    public UserAuthResponse authenticateUser(String email, String password) {
        return null;
    }
}
