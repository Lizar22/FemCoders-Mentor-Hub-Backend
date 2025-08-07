package com.fcmh.femcodersmentorhub.auth.dtos;

import com.fcmh.femcodersmentorhub.auth.Role;

public record UserAuthResponse(
        String username,
        String email,
        Role role
){
}
