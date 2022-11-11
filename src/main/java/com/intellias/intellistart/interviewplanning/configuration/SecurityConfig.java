package com.intellias.intellistart.interviewplanning.configuration;

import com.intellias.intellistart.interviewplanning.security.jwt.JwtConfigurer;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Configuration class of Spring Security.
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtTokenProvider jwtTokenProvider;

  private static final String INTERVIEWER_ENDPOINT = "/interviewers/**";
  private static final String ALL_ENDPOINTS = "/**";
  private static final String BOOKINGS_ENDPOINT = "/bookings/**";
  private static final String CANDIDATES_ENDPOINT = "/candidates/**";
  private static final String WEEK_ENDPOINT = "/users/**";
  private static final String COORDINATORS_ENDPOINT = "users/coordinators";
  private static final String LOGIN_ENDPOINT = "/authenticate";

  @Autowired
  public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .httpBasic().disable()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(LOGIN_ENDPOINT).permitAll()
        .antMatchers(ALL_ENDPOINTS).hasAuthority("COORDINATOR")
        .anyRequest().authenticated()
        .and()
        .apply(new JwtConfigurer(jwtTokenProvider));
  }


}
