package com.fcmh.femcodersmentorhub.requests.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class MentoringRequestNotFoundException extends RuntimeException {
    public MentoringRequestNotFoundException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.REQUEST_02;
    }
}
