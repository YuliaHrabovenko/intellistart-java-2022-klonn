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
  protected final String errorMessage;
  protected final HttpStatus errorCode;

  public ResourceException(String errorMessage, HttpStatus errorCode) {
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
  }
}
