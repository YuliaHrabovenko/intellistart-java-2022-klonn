package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.dto.JwtRequest;
import com.intellias.intellistart.interviewplanning.dto.UserInfo;
import com.intellias.intellistart.interviewplanning.utils.FacebookTokenUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller.
 */
@RestController
public class AuthController {

  /**
   * Generate token for user if authentication is successful.
   *
   * @param facebookJwtRequest token from Facebook
   * @return own generated token
   */
  @PostMapping(path = "/authenticate")
  public UserInfo postToken(@RequestBody JwtRequest facebookJwtRequest) {
    UserInfo token = FacebookTokenUtil.getUserInfo(facebookJwtRequest);

    return FacebookTokenUtil.getUserInfo(facebookJwtRequest);
  }
}
