package com.fcmh.femcodersmentorhub.utils;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import com.fcmh.femcodersmentorhub.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTestHelper {

    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    public void existingUser(String username, String email, String password, Role role) {
        UserAuth user = UserAuth.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        userAuthRepository.save(user);
    }
}
