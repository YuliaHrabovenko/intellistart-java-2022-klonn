package interviewplanning.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto for user request.
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
  @NotNull(message = "email has to be present")
  @Email(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
  private String email;
}
