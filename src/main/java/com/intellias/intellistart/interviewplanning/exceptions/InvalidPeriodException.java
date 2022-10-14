package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for invalid candidate time slot period.
 */
@Getter
@Setter
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InvalidPeriodException extends RuntimeException {
  public InvalidPeriodException(String message) {
    super(message);
  }
}
