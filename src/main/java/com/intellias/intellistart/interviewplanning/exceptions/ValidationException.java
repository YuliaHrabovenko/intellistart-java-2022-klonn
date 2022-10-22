package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Validation exception class.
 */
@Getter
@Setter
public class ValidationException extends AbstractCommonException {

  public ValidationException(String message) {
    super(message);
    this.httpStatus = HttpStatus.BAD_REQUEST;
  }
}
