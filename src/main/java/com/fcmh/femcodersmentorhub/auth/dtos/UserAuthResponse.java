package com.fcmh.femcodersmentorhub.auth.dtos;

import com.fcmh.femcodersmentorhub.security.Role;

public record UserAuthResponse(
        String username,
        String email,
        Role role
){
}
