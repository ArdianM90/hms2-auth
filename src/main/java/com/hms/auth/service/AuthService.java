package com.hms.auth.service;

import static com.hms.generated.jooq.tables.AppUser.APP_USER;

import com.hms.auth.exception.BusinessException;
import com.hms.auth.model.RegisterRequest;
import com.hms.generated.jooq.Tables;
import com.hms.generated.jooq.tables.records.AppUserRecord;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final DSLContext dsl;

  public UUID register(RegisterRequest request) {

    UUID id = UUID.randomUUID();

    dsl.insertInto(APP_USER)
        .set(APP_USER.USER_ID, id)
        .set(APP_USER.FIRST_NAME, request.firstName())
        .set(APP_USER.LAST_NAME, request.lastName())
        .set(APP_USER.EMAIL, request.email())
        .set(APP_USER.ROLE_CODE, request.roleCode())
        .execute();
    return id;
  }

  public void inactivate(UUID userId) {
    AppUserRecord user = dsl.selectFrom(APP_USER).where(APP_USER.USER_ID.eq(userId)).fetchOne();
    if (user == null) {
      throw new BusinessException("Nie znaleziono użytkownika.");
    }

    user.setIsActive(false);
    user.setEmail(user.getEmail() + "_" + UUID.randomUUID());
    user.update();
  }

  public void setPassword(String email, String encode) {
    dsl.update(Tables.APP_USER)
        .set(Tables.APP_USER.PASSWORD_HASH, encode)
        .where(Tables.APP_USER.EMAIL.eq(email))
        .execute();
  }
}
