package interviewplanning.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Resource exception handler.
 */
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
                         AuthenticationException ex)
        throws IOException, ServletException {
      AbstractCommonException commonEx = new AuthException(AuthException.INVALID_AUTH_TOKEN);
      response.getWriter().print(new ObjectMapper().writeValueAsString(commonEx));
      response.setContentType("application/json");
      response.setStatus(commonEx.getStatusCode());
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
      AbstractCommonException commonEx = new AuthException(AuthException.ACCESS_UNAUTHORIZED);
      response.getWriter().print(new ObjectMapper().writeValueAsString(commonEx));
      response.setContentType("application/json");
      response.setStatus(commonEx.getStatusCode());
    }
  }

  @ResponseBody
  @ExceptionHandler(AbstractCommonException.class)
  ResponseEntity<?>  handleCommonException(HttpServletRequest request, Throwable ex) {
    AbstractCommonException commonEx = (AbstractCommonException) ex;
    return new ResponseEntity<>(commonEx, HttpStatus.resolve(commonEx.getStatusCode()));
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
  @ResponseBody
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    String errorMessage = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(","));

    AbstractCommonException commonEx =
        new ValidationException(400, "invalid_parameter", errorMessage);

    return new ResponseEntity<>(commonEx, HttpStatus.resolve(commonEx.getStatusCode()));
  }

  @ResponseBody
  @ExceptionHandler(Throwable.class)
  ResponseEntity<?>  handleInnerServerException(HttpServletRequest request, Throwable ex) {
    AbstractCommonException commonEx =
        new InternalErrorException(InternalErrorException.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(commonEx, HttpStatus.resolve(commonEx.getStatusCode()));
  }
}
