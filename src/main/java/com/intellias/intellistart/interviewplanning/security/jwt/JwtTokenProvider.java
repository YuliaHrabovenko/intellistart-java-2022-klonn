package com.intellias.intellistart.interviewplanning.security.jwt;

import com.intellias.intellistart.interviewplanning.dto.JwtRequest;
import com.intellias.intellistart.interviewplanning.dto.UserInfo;
import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.utils.FacebookTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Class to create, validate and handle JWT tokens.
 */
@Component
public class JwtTokenProvider {

  @Value("${jwt.token.secret}")
  private String secret;
  @Value("${jwt.token.expired}")
  private long validityInMilliseconds;
  private FacebookTokenUtil facebookTokenUtil;
  private UserRepository userRepository;

  @Autowired
  public JwtTokenProvider(FacebookTokenUtil facebookTokenUtil, UserRepository userRepository) {
    this.facebookTokenUtil = facebookTokenUtil;
    this.userRepository = userRepository;
  }

  /**
   * Create a JWT token.
   *
   * @param facebookJwtRequest DTO that contains Facebook access token.
   * @return JWT token
   */
  public String createToken(JwtRequest facebookJwtRequest) {
    UserInfo userInfo = facebookTokenUtil.getUserInfo(facebookJwtRequest);

    Optional<User> user = userRepository.findUserByEmail(userInfo.getEmail());

    Claims claims = Jwts.claims().setSubject(userInfo.getEmail());
    claims.put("name", userInfo.getName());
    if (user.isPresent()) {
      //If user exists in database, get his/her role
      claims.put("role", user.get().getRole());
    } else {
      claims.put("role", UserRole.CANDIDATE);
    }

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
  }

  /**
   * Provide authentication.
   *
   * @param token JWT token
   * @return authentication token
   */
  public Authentication getAuthentication(String token) {
    Optional<User> user = this.userRepository.findUserByEmail(getEmail(token));
    UserDetails userDetails = null;
    if (user.isPresent()) {
      userDetails = JwtUserFactory.create(user.get());
    } else {
      userDetails = JwtUserFactory.create(token, secret);
    }

    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private String getEmail(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
  }

  public String resolveToken(HttpServletRequest req) {
    return req.getHeader("Authorization");
  }

  /**
   * Validate token.
   *
   * @param token JWT token
   * @return true if token is valid, otherwise false or throws exception
   */
  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

      if (claims.getBody().getExpiration().before(new Date())) {
        return false;
      }

      return true;
    } catch (JwtException | IllegalArgumentException e) {
      throw new ValidationException(ExceptionMessage.INVALID_JWT_TOKEN.getMessage());
    }
  }


}
