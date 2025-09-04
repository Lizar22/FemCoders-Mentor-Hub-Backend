package com.fcmh.femcodersmentorhub.shared.exceptions;

public enum ErrorCode {
    AUTH_01, // User not found
    AUTH_02, // Invalid credentials
    AUTH_03, // Email or username already registered
    AUTH_04, // Invalid token
    AUTH_05, // Expired token

    VALIDATION_01, //Validation error

    MENTOR_01, // Mentor profile not found
    MENTOR_02, // Mentor profile already exists

    REQUEST_01, // Invalid mentoring request
    REQUEST_02, // Mentoring request not found
    REQUEST_03, // Unauthorized mentoring request access

    SERVER_01 // Internal server error
}