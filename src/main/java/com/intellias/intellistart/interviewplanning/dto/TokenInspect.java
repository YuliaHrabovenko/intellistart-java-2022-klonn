package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for Graph API endpoint token check.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenInspect {
  @JsonProperty("data")
  private TokenData data;
}
