package interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request jwt token DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {
  @JsonProperty("access_token")
  String token;
}
