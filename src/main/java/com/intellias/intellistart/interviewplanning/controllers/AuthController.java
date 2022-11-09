package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.dto.JwtRequest;
import com.intellias.intellistart.interviewplanning.dto.UserInfo;
import com.intellias.intellistart.interviewplanning.utils.FacebookTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller.
 */
@RestController
public class AuthController {

  private final FacebookTokenUtil facebookTokenUtil;

  @Autowired
  public AuthController(FacebookTokenUtil facebookTokenUtil) {
    this.facebookTokenUtil = facebookTokenUtil;
  }

  /**
   * Generate token for user if authentication is successful.
   *
   * @param facebookJwtRequest token from Facebook
   * @return own generated token
   */
  @PostMapping(path = "/authenticate")
  public UserInfo postToken(@RequestBody JwtRequest facebookJwtRequest) {
    UserInfo userInfo = facebookTokenUtil.getUserInfo(facebookJwtRequest);
    // TODO: generate token by user info and role
    return userInfo;
  }
}
