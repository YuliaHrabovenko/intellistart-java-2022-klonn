package interviewplanning.security;

import interviewplanning.dto.JwtRequest;
import interviewplanning.dto.UserInfo;
import interviewplanning.exceptions.AuthException;
import interviewplanning.models.User;
import interviewplanning.models.UserRole;
import interviewplanning.repositories.UserRepository;
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
  private final FacebookToken facebookToken;
  private final UserRepository userRepository;

  @Autowired
  public JwtTokenProvider(FacebookToken facebookToken, UserRepository userRepository) {
    this.facebookToken = facebookToken;
    this.userRepository = userRepository;
  }

  /**
   * Create a JWT token.
   *
   * @param facebookJwtRequest DTO that contains Facebook access token.
   * @return JWT token
   */
  public String createToken(JwtRequest facebookJwtRequest) {
    UserInfo userInfo = facebookToken.getUserInfo(facebookJwtRequest);

    Optional<User> user = userRepository.findByEmail(userInfo.getEmail());

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
    Optional<User> user = this.userRepository.findByEmail(getEmail(token));
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

  /**
   * Get token from the request.
   *
   * @param req request
   * @return token if found
   */
  public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length());
    }
    return null;
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
      throw new AuthException(AuthException.INVALID_AUTH_TOKEN);
    }
  }


}