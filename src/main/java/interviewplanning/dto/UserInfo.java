package interviewplanning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User info DTO.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
  private String name;
  private String email;
}
