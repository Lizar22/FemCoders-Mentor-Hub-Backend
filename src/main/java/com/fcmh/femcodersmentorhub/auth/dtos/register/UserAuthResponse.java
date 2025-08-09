package com.fcmh.femcodersmentorhub.auth.dtos.register;

import com.fcmh.femcodersmentorhub.security.Role;

public record UserAuthResponse(
        String username,
        String email,
        Role role
){
}
