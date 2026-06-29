package com.hms.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
  private static final String GENERIC_MSG =
      "Żądanie zawiera nieprawidłowe dane. Sprawdź wprowadzone wartości.";
  private final HttpStatus status;

  public BusinessException() {
    super(GENERIC_MSG);
    this.status = HttpStatus.BAD_REQUEST;
  }

  public BusinessException(HttpStatus status) {
    super(GENERIC_MSG);
    this.status = status;
  }

  public BusinessException(String message) {
    super(message);
    this.status = HttpStatus.BAD_REQUEST;
  }

  public BusinessException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}
