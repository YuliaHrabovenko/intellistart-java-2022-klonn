package interviewplanning.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import interviewplanning.dto.JwtRequest;
import interviewplanning.dto.UserInfo;
import interviewplanning.exceptions.AuthException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Class to process user's Facebook token.
 */
@Component
public class FacebookToken {
  @Value("${spring.security.oauth2.client.clientId}")
  private String clientId;

  @Value("${spring.security.oauth2.client.clientSecret}")
  private String clientSecret;

  private static final String ACCESS_TOKEN_URI =
      "https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&grant_type=client_credentials";
  private static final String ACCESS_TOKEN_DATA_URI =
      "https://graph.facebook.com/debug_token?input_token=%s&access_token=%s";
  private static final String USER_INFO_URI =
      "https://graph.facebook.com/%s?fields=name,email&access_token=%s";


  /**
   * DTO for Graph API endpoint token check.
   */
  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  static class TokenInspect {
    @JsonProperty("data")
    private TokenData data;
  }

  /**
   * DTO for inspected token data.
   */
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  static class TokenData {
    @JsonProperty("app_id")
    private String appId;
    private String type;
    private String application;
    @JsonProperty("expires_at")
    private String expiresAt;
    @JsonProperty("data_access_expires_at")
    private String dataExpiresAt;
    @JsonProperty("is_valid")
    private String isValid;
    private String[] scopes;
    @JsonProperty("user_id")
    private String userId;
  }

  /**
   * Get an app access token from Facebook.
   *
   * @return obtained access token
   */
  public JwtRequest getAccessToken() {
    String uri = String.format(ACCESS_TOKEN_URI, clientId, clientSecret);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<JwtRequest> response = restTemplate.getForEntity(uri, JwtRequest.class);
    return response.getBody();
  }

  /**
   * Get data from user token.
   *
   * @param inputToken token provided by user
   * @return user data from inspected token
   */
  public TokenInspect inspectAccessToken(JwtRequest inputToken) {
    JwtRequest accessToken = getAccessToken();
    if (accessToken == null) {
      throw new AuthException(AuthException.INVALID_AUTH_TOKEN);
    }
    String uri =
        String.format(ACCESS_TOKEN_DATA_URI, inputToken.getToken(), accessToken.getToken());
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<TokenInspect> response = restTemplate.getForEntity(uri, TokenInspect.class);
    return response.getBody();
  }

  /**
   * Get user's name and email info.
   *
   * @param inputToken token provided by user
   * @return user info
   */
  public UserInfo getUserInfo(JwtRequest inputToken) {
    TokenInspect token = inspectAccessToken(inputToken);
    if (token == null) {
      throw new AuthException(AuthException.INVALID_AUTH_TOKEN);
    }
    String uri = String.format(USER_INFO_URI, token.getData().getUserId(), inputToken.getToken());
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<UserInfo> response = restTemplate.getForEntity(uri, UserInfo.class);
    return response.getBody();
  }

}
