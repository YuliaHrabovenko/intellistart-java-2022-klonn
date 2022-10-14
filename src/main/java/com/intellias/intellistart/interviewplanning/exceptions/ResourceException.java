package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Resource exception message and status.
 */
@Getter
@Setter
public class ResourceException {
  protected final String message;
  protected final HttpStatus httpStatus;

  public ResourceException(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
