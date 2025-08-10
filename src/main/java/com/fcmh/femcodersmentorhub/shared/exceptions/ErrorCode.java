package com.fcmh.femcodersmentorhub.shared.exceptions;

public enum ErrorCode {

    AUTH_01, // User not found
    AUTH_02, // Invalid credentials
    AUTH_03, // Email already registered
    AUTH_04, // Invalid token

    VALIDATION_01, //Validation error

    SERVER_01 // Internal server error
}
