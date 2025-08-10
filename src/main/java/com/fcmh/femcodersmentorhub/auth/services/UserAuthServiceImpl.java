package com.fcmh.femcodersmentorhub.auth.services;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.login.LoginResponse;
import com.fcmh.femcodersmentorhub.auth.exceptions.InvalidCredentialsException;
import com.fcmh.femcodersmentorhub.auth.exceptions.UserAlreadyExistsException;
import com.fcmh.femcodersmentorhub.auth.exceptions.UserNotFoundException;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthMapper;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthRequest;
import com.fcmh.femcodersmentorhub.auth.dtos.register.UserAuthResponse;
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
        UserAuth user = userAuthRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return UserAuthMapper.entityToDto(user);
    }

    @Override
    public UserAuthResponse addUser(UserAuthRequest userAuthRequest) {

        if (userAuthRepository.existsByEmail(userAuthRequest.email())) {
            throw new UserAlreadyExistsException("The email is already registered: " + userAuthRequest.email());
        }

        if (userAuthRepository.existsByUsername(userAuthRequest.username())) {
            throw new UserAlreadyExistsException("The username is already registered: " + userAuthRequest.username());
        }

        UserAuth newUser = userAuthMapper.dtoToEntity(userAuthRequest);
        newUser.setPassword(passwordEncoder.encode(userAuthRequest.password()));
        UserAuth savedUser = userAuthRepository.save(newUser);
        return userAuthMapper.entityToDto(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        UserAuth user = userAuthRepository.findByEmail(loginRequest.identifier()).orElseThrow(() -> new UserNotFoundException(loginRequest.identifier()));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException(loginRequest.identifier());
        }

        return new LoginResponse(
                user.getUsername(),
                user.getEmail()
        );
    }
}
