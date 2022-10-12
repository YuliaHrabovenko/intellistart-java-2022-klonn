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
public class InvalidInterviewerPeriodException extends RuntimeException {
  public InvalidInterviewerPeriodException(String message) {
    super(message);
  }
}