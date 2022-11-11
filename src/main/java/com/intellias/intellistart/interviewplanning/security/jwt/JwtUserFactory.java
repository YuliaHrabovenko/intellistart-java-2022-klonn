package com.intellias.intellistart.interviewplanning.security.jwt;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Class for creating JwtUser.
 */
public final class JwtUserFactory {

  public JwtUserFactory() {

  }

  /**
   * Create JwtUser based on User that is present in database.
   *
   * @param user User model
   * @return JwtUser
   */
  public static JwtUser create(User user) {
    return new JwtUser(
        user.getName(),
        user.getEmail(),
        user.getRole(),
        mapToGrantedAuthorities(user.getRole())
    );
  }

  /**
   * Create JwtUser based on User that is not present in database.
   *
   * @param token  JWT token
   * @param secret secret keyword to encode JWT token
   * @return JwtUser
   */
  public static JwtUser create(String token, String secret) {
    Claims jwtBody = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    return new JwtUser(
        jwtBody.get("name", String.class),
        jwtBody.getSubject()
    );
  }

  private static List<GrantedAuthority> mapToGrantedAuthorities(UserRole userRole) {
    return List.of(new SimpleGrantedAuthority(userRole.toString()));
  }
}
