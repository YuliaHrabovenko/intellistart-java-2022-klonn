package com.intellias.intellistart.interviewplanning.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Resource exception handler.
 */
@Slf4j
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

  /**
   * Rejects every unauthenticated request.
   */
  public static class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public JwtAuthenticationEntryPoint() {
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
        throws IOException, ServletException {
      log.error("Auth error on " + request.getMethod() + " " + request.getRequestURI() + ":"
          + authException.getMessage(), authException);
      ExceptionDetail exceptionDetail = new ExceptionDetail(
          HttpStatus.UNAUTHORIZED,
          authException.getMessage()
      );

      response.getWriter().print(new ObjectMapper().writeValueAsString(exceptionDetail));
      response.setContentType("application/json");
      response.setStatus(exceptionDetail.getErrorCode().value());
    }

  }

  /**
   * Rejects every unauthorized request.
   */
  public static class CustomAccessDeniedHandler implements AccessDeniedHandler {

    public CustomAccessDeniedHandler() {
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
        throws IOException, ServletException {
      ExceptionDetail exceptionDetail = new ExceptionDetail(
          HttpStatus.FORBIDDEN,
          accessDeniedException.getMessage()
      );
      log.error(
          "Authorization error on " + request.getMethod() + " " + request.getRequestURI() + ":"
              + accessDeniedException.getMessage(), accessDeniedException);
      response.getWriter().print(new ObjectMapper().writeValueAsString(exceptionDetail));
      response.setContentType("application/json");
      response.setStatus(exceptionDetail.getErrorCode().value());
    }
  }

  /**
   * Exception handler for RuntimeException type exceptions.
   *
   * @param resourceException custom exception classes
   * @return message and status on response to user
   */
  @ExceptionHandler(value = {NotFoundException.class, ValidationException.class})
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

  /**
   * Authentication and authorization exceptions handler.
   *
   * @param errorException HttpClientErrorException errorException
   * @return message and status on response to user
   */
  @ExceptionHandler(value = {HttpClientErrorException.class})
  public ResponseEntity<Object> handleHttpClientErrorException(
      HttpClientErrorException errorException) {
    String errorMessage = errorException.getResponseBodyAsString();
    JSONObject obj = new JSONObject(errorMessage);

    ExceptionDetail exceptionDetail = new ExceptionDetail(
        errorException.getStatusCode(),
        obj.getJSONObject("error").get("message").toString()
    );
    return new ResponseEntity<>(exceptionDetail, errorException.getStatusCode());
  }

}
