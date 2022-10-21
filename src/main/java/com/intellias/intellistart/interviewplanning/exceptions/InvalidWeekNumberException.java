package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Exception for invalid week number.
 */
@Getter
@Setter
public class InvalidWeekNumberException extends AbstractResourceRuntimeException{
  public InvalidWeekNumberException(String message) {
    super(message);
    this.httpStatus = HttpStatus.BAD_REQUEST;
  }
}
