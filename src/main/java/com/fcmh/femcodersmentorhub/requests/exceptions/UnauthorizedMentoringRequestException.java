package com.fcmh.femcodersmentorhub.requests.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class UnauthorizedMentoringRequestException extends RuntimeException {
    public UnauthorizedMentoringRequestException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.REQUEST_03;
    }
}