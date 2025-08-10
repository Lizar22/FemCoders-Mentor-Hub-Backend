package com.fcmh.femcodersmentorhub.security.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.AUTH_04;
    }
}
