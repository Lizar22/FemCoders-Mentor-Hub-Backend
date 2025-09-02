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
import com.fcmh.femcodersmentorhub.emails.EmailService;
import com.fcmh.femcodersmentorhub.security.CustomUserDetails;
import com.fcmh.femcodersmentorhub.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthServiceImpl implements UserAuthService {
    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

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

        UserAuth newUser = UserAuthMapper.dtoToEntity(userAuthRequest);
        newUser.setPassword(passwordEncoder.encode(userAuthRequest.password()));
        UserAuth savedUser = userAuthRepository.save(newUser);

        emailService.sendWelcomeNotification(savedUser.getEmail(), savedUser.getUsername());

        return UserAuthMapper.entityToDto(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.identifier(),
                            loginRequest.password()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            return new LoginResponse(token);

        } catch (UsernameNotFoundException exception) {
            throw new UserNotFoundException("User not found: " + loginRequest.identifier());
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException("Invalid credentials for: " + loginRequest.identifier());
        }
    }
}