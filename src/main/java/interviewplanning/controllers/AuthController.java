package interviewplanning.controllers;

import interviewplanning.dto.JwtRequest;
import interviewplanning.dto.JwtResponse;
import interviewplanning.dto.UserDto;
import interviewplanning.security.JwtTokenProvider;
import interviewplanning.security.JwtUser;
import interviewplanning.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller.
 */
@RestController
public class AuthController {
  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;

  @Autowired
  public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userService = userService;
  }

  /**
   * Generate token for user if authentication is successful.
   *
   * @param facebookJwtRequest token from Facebook
   * @return own generated token
   */
  @PostMapping(path = "/authenticate")
  public JwtResponse getJwtToken(@RequestBody JwtRequest facebookJwtRequest) {
    return new JwtResponse(jwtTokenProvider.createToken(facebookJwtRequest));
  }

  @GetMapping("/me")
  public UserDto getMe(Authentication authentication) {
    JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
    return userService.getByEmail(jwtUser.getEmail());
  }
}

