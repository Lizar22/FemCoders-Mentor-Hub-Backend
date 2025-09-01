package com.fcmh.femcodersmentorhub.mentors.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class MentorProfileAlreadyExistsException extends RuntimeException {
    public MentorProfileAlreadyExistsException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.MENTOR_02;
    }
}