package com.fcmh.femcodersmentorhub.mentors.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class MentorProfileNotFoundException extends RuntimeException {
    public MentorProfileNotFoundException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.MENTOR_01;
    }
}

