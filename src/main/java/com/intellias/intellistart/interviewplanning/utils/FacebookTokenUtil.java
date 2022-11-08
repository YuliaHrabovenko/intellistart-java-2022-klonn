package com.intellias.intellistart.interviewplanning.utils;

import com.intellias.intellistart.interviewplanning.dto.JwtRequest;
import com.intellias.intellistart.interviewplanning.dto.TokenInspect;
import com.intellias.intellistart.interviewplanning.dto.UserInfo;
import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Util class to process user's Facebook token.
 */
public class FacebookTokenUtil {
  private static Dotenv dotenv = Dotenv.load();
  private static final String CLIENT_ID = dotenv.get("CLIENT_ID");
  private static final String CLIENT_SECRET = dotenv.get("CLIENT_SECRET");

  private static final String ACCESS_TOKEN_URI =
      "https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&grant_type=client_credentials";
  private static final String ACCESS_TOKEN_DATA_URI =
      "https://graph.facebook.com/debug_token?input_token=%s&access_token=%s";
  private static final String USER_INFO_URI =
      "https://graph.facebook.com/%s?fields=name,email&access_token=%s";

  /**
   * Get an app access token from Facebook.
   *
   * @return obtained access token
   */
  public static JwtRequest getAccessToken() {
    String uri = String.format(ACCESS_TOKEN_URI, CLIENT_ID, CLIENT_SECRET);
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
  public static TokenInspect inspectAccessToken(JwtRequest inputToken) {
    JwtRequest accessToken = getAccessToken();
    if (accessToken == null) {
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
          ExceptionMessage.INVALID_AUTH_TOKEN.getMessage());
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
  public static UserInfo getUserInfo(JwtRequest inputToken) {
    TokenInspect token = inspectAccessToken(inputToken);
    if (token == null) {
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
          ExceptionMessage.INVALID_AUTH_TOKEN.getMessage());
    }
    String uri = String.format(USER_INFO_URI, token.getData().getUserId(), inputToken.getToken());
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<UserInfo> response = restTemplate.getForEntity(uri, UserInfo.class);
    return response.getBody();
  }

}
