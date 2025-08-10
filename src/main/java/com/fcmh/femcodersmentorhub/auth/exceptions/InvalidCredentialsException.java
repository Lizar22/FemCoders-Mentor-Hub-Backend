package com.fcmh.femcodersmentorhub.auth.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }

  public ErrorCode getErrorCode() {
    return ErrorCode.AUTH_02;
  }
}
