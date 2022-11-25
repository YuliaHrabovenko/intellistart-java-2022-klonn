package com.intellias.intellistart.interviewplanning.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract resource exception.
 */
@Getter
@Setter
@JsonIgnoreProperties
    ({"stackTrace", "cause", "localizedMessage", "suppressed", "statusCode", "message"})
public abstract class AbstractCommonException extends RuntimeException {
  private final String errorCode;
  private final int statusCode;
  private final String errorMessage;

  /**
   * Constructor.
   *
   * @param statusCode   status
   * @param errorCode    error code
   * @param errorMessage message
   */
  public AbstractCommonException(int statusCode, String errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.statusCode = statusCode;
    this.errorMessage = errorMessage;
  }

  /**
   * Detail constructor.
   *
   * @param detail exception description
   */
  public AbstractCommonException(Detail detail) {
    this.errorCode = detail.errorCode;
    this.statusCode = detail.statusCode;
    this.errorMessage = detail.errorMessage;
  }

  /**
   * Exception description.
   */
  public static class Detail {
    final String errorCode;
    final String errorMessage;
    final int statusCode;

    /**
     * Detail constructor.
     *
     * @param statusCode   status code
     * @param errorCode    error code
     * @param errorMessage error message
     */
    public Detail(int statusCode, String errorCode, String errorMessage) {
      this.errorCode = errorCode;
      this.errorMessage = errorMessage;
      this.statusCode = statusCode;
    }
  }
}
