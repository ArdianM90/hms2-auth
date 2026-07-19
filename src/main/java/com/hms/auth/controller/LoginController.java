package com.hms.auth.controller;

import com.hms.auth.model.AppUserDto;
import com.hms.auth.model.IsInitialPasswordResponse;
import com.hms.auth.model.LoginCheckRequest;
import com.hms.auth.service.AppUserService;
import com.hms.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

  private final AuthService authService;
  private final AppUserService appUserService;
  private final PasswordEncoder passwordEncoder;

  @Value("${hms.web.url}")
  private String hmsWebUrl;

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/set-password")
  public String form(HttpSession session) {
    if (session.getAttribute("pendingPasswordEmail") == null) {
      return "redirect:/login";
    }
    return "set-password";
  }

  @PostMapping("/check-first-login")
  public ResponseEntity<IsInitialPasswordResponse> checkPassword(
      @RequestBody LoginCheckRequest request, HttpSession session) {

    AppUserDto user = appUserService.findByEmail(request.email());
    if (user == null || !user.isDefaultPassword()) {
      return ResponseEntity.ok(new IsInitialPasswordResponse(false));
    }
    if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
      return ResponseEntity.ok(new IsInitialPasswordResponse(false));
    }
    session.setMaxInactiveInterval(300);
    session.setAttribute("pendingPasswordEmail", request.email());
    return ResponseEntity.ok(new IsInitialPasswordResponse(true));
  }

  @PostMapping("/set-password")
  public String submitPassword(
      @RequestParam String newPassword,
      @RequestParam String confirmPassword,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {

    String email = (String) request.getSession().getAttribute("pendingPasswordEmail");
    if (email == null) {
      return "redirect:/login";
    }

    if (!newPassword.equals(confirmPassword) || newPassword.length() < 8) {
      redirectAttributes.addFlashAttribute(
          "error", "Hasła muszą być identyczne i mieć min. 8 znaków");
      return "redirect:/set-password";
    }

    authService.setPassword(email, passwordEncoder.encode(newPassword));
    request.getSession().removeAttribute("pendingPasswordEmail");
    redirectAttributes.addFlashAttribute(
        "success", "Hasło zostało ustawione. Możesz się teraz zalogować.");
    return "redirect:/login";
  }
}
