package com.fcmh.femcodersmentorhub.security.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.AUTH_05;
    }
}
