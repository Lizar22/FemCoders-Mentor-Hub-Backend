package com.fcmh.femcodersmentorhub.security;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import com.fcmh.femcodersmentorhub.auth.repository.UserAuthRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserAuthRepository userAuthRepository;

    public CustomUserDetailsService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<UserAuth> user = userAuthRepository.findByUsername(identifier);

        if (user.isEmpty()) {
            user = userAuthRepository.findByEmail(identifier);
        }

        return user.map(userEntity -> new CustomUserDetails(userEntity))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
