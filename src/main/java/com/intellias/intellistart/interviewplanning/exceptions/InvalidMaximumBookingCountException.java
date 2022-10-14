package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for invalid interviewer time slot period.
 */
@Getter
@Setter
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InvalidMaximumBookingCountException extends RuntimeException {
  public InvalidMaximumBookingCountException(String message) {
    super(message);
  }
}