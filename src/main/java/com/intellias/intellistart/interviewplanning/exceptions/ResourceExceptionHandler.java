package com.intellias.intellistart.interviewplanning.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Resource exception handler.
 */
@ControllerAdvice
public class ResourceExceptionHandler {
  /**
   * Exception handler for custom exception classes of RuntimeException type.
   *
   * @param resourceNotFoundException custom exception classes
   * @return message and status on response to user
   */
  @ExceptionHandler(value = {ResourceNotFoundException.class, BookingDoneException.class,
      InvalidPeriodException.class, InvalidResourceException.class})
  public ResponseEntity<Object> handleException(AbstractResourceRuntimeException
                                                    resourceNotFoundException) {
    ResourceException resourceException = new ResourceException(
        resourceNotFoundException.getMessage(),
        resourceNotFoundException.getHttpStatus()
    );

    return new ResponseEntity<>(resourceException, resourceNotFoundException.getHttpStatus());
  }

}