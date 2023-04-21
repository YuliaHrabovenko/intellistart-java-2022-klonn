package interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * Authentication exception class.
 */
@Getter
@Setter
public class AuthException extends AbstractCommonException {

  public static final Detail ACCESS_UNAUTHORIZED =
      new Detail(403, "not_authorized", "You are not authorized to use this functionality");
  public static final Detail INVALID_AUTH_TOKEN =
      new Detail(401, "not_authenticated", "Invalid or expired authentication token");

  public AuthException(Detail detail) {
    super(detail);
  }
}
