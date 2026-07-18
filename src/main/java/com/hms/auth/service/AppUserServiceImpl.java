package com.hms.auth.service;

import static com.hms.generated.jooq.Tables.APP_USER;

import com.hms.auth.model.AppUserDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

  private final DSLContext dsl;

  @Override
  public AppUserDto findByEmail(String email) {
    return dsl.select(
            APP_USER.USER_ID.as("id"),
            APP_USER.EMAIL,
            APP_USER.PASSWORD_HASH,
            APP_USER.ROLE_CODE,
            APP_USER.IS_INITIAL_PASSWORD)
        .from(APP_USER)
        .where(APP_USER.EMAIL.eq(email))
        .fetchOneInto(AppUserDto.class);
  }
}
