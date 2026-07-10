package com.hms.auth.service;

import com.hms.auth.model.AppUserDto;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@NullMarked
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

  private final AppUserService appUserService;

  @Override
  public UserDetails loadUserByUsername(String email) {

    AppUserDto appUserDto = appUserService.findByEmail(email);

    if (appUserDto == null) {
      throw new UsernameNotFoundException(email);
    }

    return User.withUsername(appUserDto.email())
        .password(appUserDto.passwordHash())
        .authorities("ROLE_" + appUserDto.roleCode().toUpperCase())
        .build();
  }
}
