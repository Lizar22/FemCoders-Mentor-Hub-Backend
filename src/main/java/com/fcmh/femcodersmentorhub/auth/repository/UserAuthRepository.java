package com.fcmh.femcodersmentorhub.auth.repository;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository <UserAuth, Long> {
}
