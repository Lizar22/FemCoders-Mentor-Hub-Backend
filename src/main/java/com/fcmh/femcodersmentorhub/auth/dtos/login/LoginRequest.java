package com.fcmh.femcodersmentorhub.auth.dtos.login;

public record LoginRequest(
        String identifier,
        String password
) {
}
