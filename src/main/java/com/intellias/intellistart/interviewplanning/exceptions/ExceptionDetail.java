package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Resource exception message and status.
 */
@Getter
@Setter
public class ExceptionDetail {
  protected final HttpStatus errorCode;
  protected final String errorMessage;

  public ExceptionDetail(HttpStatus errorCode, String errorMessage) {
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
  }
}
