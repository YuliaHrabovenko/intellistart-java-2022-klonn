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

  //COORDINATOR endpoints

  private static final String COORDINATOR = "COORDINATOR";
  private static final String DASHBOARD_ENDPOINT = "/weeks/{weekId}/dashboard";
  private static final String UPD_INTER_SLOT_ENDP = "/interviewers/{interviewerId}/slots/{slotId}";
  private static final String BOOKINGS_ENDPOINTS = "/bookings/**";
  private static final String USERS_ENDPOINTS = "/users/**";

  //INTERVIEWER endpoints

  private static final String INTERVIEWER = "INTERVIEWER";

  private static final String INTERVIEWERS_ENDPOINTS = "/interviewers/**";

  //CANDIDATE endpoints

  private static final String CANDIDATE = "CANDIDATE";

  private static final String CANDIDATES_ENDPOINTS = "/candidates/**";
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
        .antMatchers(DASHBOARD_ENDPOINT).hasAuthority(COORDINATOR)
        .antMatchers(UPD_INTER_SLOT_ENDP).hasAuthority(COORDINATOR)
        .antMatchers(BOOKINGS_ENDPOINTS).hasAuthority(COORDINATOR)
        .antMatchers(USERS_ENDPOINTS).hasAuthority(COORDINATOR)
        .antMatchers(INTERVIEWERS_ENDPOINTS).hasAuthority(INTERVIEWER)
        .antMatchers(CANDIDATES_ENDPOINTS).hasAuthority(CANDIDATE)
        .anyRequest().authenticated()
        .and()
        .apply(new JwtConfigurer(jwtTokenProvider));
  }


}
