package com.hms.auth.service;

import com.hms.auth.model.AppUserDto;

public interface AppUserService {

  AppUserDto findByEmail(String email);
}
