package com.fcmh.femcodersmentorhub.auth.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.AUTH_01;
    }
}