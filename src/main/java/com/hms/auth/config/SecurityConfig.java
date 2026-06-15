package com.hms.auth.config;

import static com.hms.generated.jooq.Tables.APP_USER;

import com.hms.generated.jooq.tables.records.AppUserRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Value("${hms.web.url}")
  private String hmsWebUrl;

  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(1)
  public SecurityFilterChain authServer(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        new OAuth2AuthorizationServerConfigurer();
    http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        .with(
            // http://localhost:8081/.well-known/openid-configuration
            authorizationServerConfigurer, configurer -> configurer.oidc(Customizer.withDefaults()))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/.well-known/**").permitAll().anyRequest().authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .exceptionHandling(
            ex -> ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain app(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.ignoringRequestMatchers("/login", "/auth/**"))
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/auth/**",
                        "/login",
                        "/.well-known/**",
                        "/images/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(
            form ->
                form.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .successHandler(
                        (request, response, authentication) ->
                            handleLoginSuccess(request, response))
                    .failureUrl("/login?error")
                    .permitAll())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(DSLContext dsl) {
    return email -> {
      AppUserRecord user = dsl.selectFrom(APP_USER).where(APP_USER.EMAIL.eq(email)).fetchOne();
      if (user == null) {
        throw new UsernameNotFoundException(email);
      }

      return User.withUsername(user.getEmail())
          .password(user.getPasswordHash())
          .roles("USER")
          .build();
    };
  }

  private void handleLoginSuccess(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
    SavedRequest savedRequest = requestCache.getRequest(request, response);

    // todo: zamiana na logger.debug()
    System.out.println(
        ">>> savedRequest: " + (savedRequest != null ? savedRequest.getRedirectUrl() : "NULL"));

    if (savedRequest != null) {
      response.sendRedirect(savedRequest.getRedirectUrl());
    } else {
      response.sendRedirect(hmsWebUrl);
    }
  }
}
