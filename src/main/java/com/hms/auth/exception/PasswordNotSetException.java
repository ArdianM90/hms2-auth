package com.hms.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class PasswordNotSetException extends AuthenticationException {
  public PasswordNotSetException(String message) {
    super(message);
  }
}
