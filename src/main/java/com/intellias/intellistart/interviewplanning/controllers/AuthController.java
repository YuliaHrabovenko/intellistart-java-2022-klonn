package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.dto.JwtRequest;
import com.intellias.intellistart.interviewplanning.dto.UserInfo;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtTokenProvider;
import com.intellias.intellistart.interviewplanning.utils.FacebookTokenUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller.
 */
@RestController
public class AuthController {
  private final JwtTokenProvider jwtTokenProvider;

  @Autowired
  public AuthController(FacebookTokenUtil facebookTokenUtil, JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  /**
   * Generate token for user if authentication is successful.
   *
   * @param facebookJwtRequest token from Facebook
   * @return own generated token
   */
  @PostMapping(path = "/authenticate")
  public ResponseEntity getJwtToken(@RequestBody JwtRequest facebookJwtRequest) {
    Map<Object, Object> response = new HashMap<>();
    response.put("token", jwtTokenProvider.createToken(facebookJwtRequest));

    return ResponseEntity.ok(response);

  }
}
