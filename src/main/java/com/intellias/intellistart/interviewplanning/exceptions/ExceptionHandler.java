package com.intellias.intellistart.interviewplanning.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Resource exception handler.
 */
@ControllerAdvice
public class ExceptionHandler {
  /**
   * Exception handler for custom exception classes of RuntimeException type.
   *
   * @param resourceNotFoundException custom exception classes
   * @return message and status on response to user
   */
  @org.springframework.web.bind.annotation.ExceptionHandler(value = {NotFoundException.class,
      ValidationException.class})
  public ResponseEntity<Object> handleException(AbstractCommonException
                                                    resourceNotFoundException) {
    ExceptionDetail exceptionDetail = new ExceptionDetail(
        resourceNotFoundException.getHttpStatus(),
        resourceNotFoundException.getMessage()
    );

    return new ResponseEntity<>(exceptionDetail, resourceNotFoundException.getHttpStatus());
  }

}
