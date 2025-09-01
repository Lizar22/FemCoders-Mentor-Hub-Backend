package com.fcmh.femcodersmentorhub.auth.exceptions;

import com.fcmh.femcodersmentorhub.shared.exceptions.ErrorCode;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String message) {
    super(message);
  }

  public ErrorCode getErrorCode() {
    return ErrorCode.AUTH_03;
  }
}