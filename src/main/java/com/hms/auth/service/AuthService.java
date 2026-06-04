package com.hms.auth.service;

import static com.hms.auth.generated.jooq.Tables.APP_USER;

import com.hms.auth.model.RegisterRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final DSLContext dsl;
  private final PasswordEncoder encoder;

  public UUID register(RegisterRequest request) {

    UUID id = UUID.randomUUID();

    dsl.insertInto(APP_USER)
        .set(APP_USER.USER_ID, id)
        .set(APP_USER.USERNAME, request.username())
        .set(APP_USER.EMAIL, request.email())
        .set(APP_USER.PASSWORD_HASH, encoder.encode(request.password()))
        .execute();
    return id;
  }
}
