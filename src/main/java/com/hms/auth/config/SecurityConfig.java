package com.hms.auth.config;

import static com.hms.auth.generated.jooq.Tables.APP_USER;

import com.hms.auth.generated.jooq.tables.records.AppUserRecord;
import org.jooq.DSLContext;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
        .with(authorizationServerConfigurer, Customizer.withDefaults())
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .formLogin(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            ex -> ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain app(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/auth/register",
                        "/oauth2/**",
                        "/login",
                        "/.well-known/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(Customizer.withDefaults())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(DSLContext dsl) {
    return username -> {
      AppUserRecord user =
          dsl.selectFrom(APP_USER).where(APP_USER.USERNAME.eq(username)).fetchOne();
      if (user == null) {
        throw new UsernameNotFoundException(username);
      }

      return User.withUsername(user.getUsername())
          .password(user.getPasswordHash())
          .roles("USER")
          .build();
    };
  }
}
