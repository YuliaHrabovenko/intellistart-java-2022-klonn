package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Exception for invalid candidate time slot period.
 */
@Getter
@Setter
public class InvalidPeriodException extends AbstractResourceRuntimeException {
  public InvalidPeriodException(String message) {
    super(message);
    this.httpStatus = HttpStatus.BAD_REQUEST;
  }

}
