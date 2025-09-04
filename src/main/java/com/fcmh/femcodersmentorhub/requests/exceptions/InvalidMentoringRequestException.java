package com.fcmh.femcodersmentorhub.requests.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class InvalidMentoringRequestException extends RuntimeException {
    public InvalidMentoringRequestException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.REQUEST_01;
    }
}