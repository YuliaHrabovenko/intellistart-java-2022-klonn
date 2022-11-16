package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO for inspected token data.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenData {
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
