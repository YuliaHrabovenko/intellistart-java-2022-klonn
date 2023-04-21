package interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * Inner error exception class.
 */
@Getter
@Setter
public class InternalErrorException extends AbstractCommonException {

  public static final Detail INTERNAL_SERVER_ERROR =
      new Detail(500, "unknown_error", "Internal server error");

  public InternalErrorException(Detail detail) {
    super(detail);
  }
}
