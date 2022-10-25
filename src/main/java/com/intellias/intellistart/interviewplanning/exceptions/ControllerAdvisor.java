package com.intellias.intellistart.interviewplanning.exceptions;

import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Resource exception handler.
 */
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
  /**
   * Exception handler for RuntimeException type exceptions.
   *
   * @param resourceException custom exception classes
   * @return message and status on response to user
   */
  @ExceptionHandler(value = {NotFoundException.class,
      ValidationException.class})
  public ResponseEntity<Object> handleException(AbstractCommonException
                                                    resourceException) {
    ExceptionDetail exceptionDetail = new ExceptionDetail(
        resourceException.getHttpStatus(),
        resourceException.getMessage()
    );

    return new ResponseEntity<>(exceptionDetail, resourceException.getHttpStatus());
  }

  /**
   * Controller layer exception handler.
   *
   * @param ex      MethodArgumentNotValidException exception
   * @param headers HttpHeaders headers
   * @param status  HttpStatus status
   * @param request WebRequest request
   * @return message and status on response to user
   */
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    String errorMessage = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(","));

    ExceptionDetail exceptionDetail = new ExceptionDetail(
        status,
        errorMessage
    );

    return new ResponseEntity<>(exceptionDetail, status);
  }

}
