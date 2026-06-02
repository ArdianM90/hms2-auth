package com.hms.auth.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException() {
    super("Nie ma takiego użytkownika");
  }
}
