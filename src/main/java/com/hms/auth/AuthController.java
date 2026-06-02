package com.hms.auth;

import com.hms.auth.model.LoginRequest;
import com.hms.auth.model.RegisterRequest;
import com.hms.auth.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService service;

  @PostMapping("/register")
  public UUID register(@RequestBody RegisterRequest request) {
    return service.register(request);
  }

  @PostMapping("/login")
  public boolean login(@RequestBody LoginRequest request) {
    System.out.println("LOGIN ENDPOINT HIT");
    return service.login(request);
  }
}
