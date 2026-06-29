package com.hms.auth.controller;

import com.hms.auth.model.RegisterRequest;
import com.hms.auth.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService service;

  @PostMapping("/register")
  public ResponseEntity<UUID> register(@RequestBody RegisterRequest request) {

    return ResponseEntity.ok(service.register(request));
  }

  @PutMapping("/{user-id}/inactivate")
  public ResponseEntity<Void> inactivate(@PathVariable("user-id") UUID userId) {
    service.inactivate(userId);
    return ResponseEntity.ok().build();
  }
}
