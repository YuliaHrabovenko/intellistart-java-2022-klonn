package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Exception when resource is invalid.
 */
@Getter
@Setter
public class InvalidResourceException extends AbstractResourceRuntimeException  {
  public InvalidResourceException(String message) {
    super(message);
    this.httpStatus = HttpStatus.BAD_REQUEST;
  }
}
