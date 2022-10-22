package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Not found exception class.
 */
@Getter
@Setter
public class NotFoundException extends AbstractCommonException {

  public NotFoundException(String message) {
    super(message);
    this.httpStatus = HttpStatus.NOT_FOUND;
  }
}
