package com.fcmh.femcodersmentorhub.auth.repository;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository <UserAuth, Long> {

    Optional<UserAuth> findByUsername(String username);
    Optional<UserAuth> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}