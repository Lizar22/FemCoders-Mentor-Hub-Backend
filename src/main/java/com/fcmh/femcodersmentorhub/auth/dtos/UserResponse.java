package com.fcmh.femcodersmentorhub.auth.dtos;

import com.fcmh.femcodersmentorhub.auth.Role;

public record UserResponse (
        String username,
        String email,
        Role role
){
}
