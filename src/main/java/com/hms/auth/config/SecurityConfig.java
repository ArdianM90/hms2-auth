package com.hms.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurity(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfigurer configurer = new OAuth2AuthorizationServerConfigurer();
    http.securityMatcher(configurer.getEndpointsMatcher())
        .with(configurer, Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurity(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/auth/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                    (request, response, authException) -> response.sendError(401)));
    return http.build();
  }
}
