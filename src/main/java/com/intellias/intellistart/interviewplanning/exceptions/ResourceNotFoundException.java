package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Exception when resource is not found.
 */
@Getter
@Setter
public class ResourceNotFoundException extends AbstractResourceRuntimeException {
  /**
   * Constructor.
   *
   * @param resourceName resource name
   * @param fieldName    field name
   * @param fieldValue   field value
   */
  public ResourceNotFoundException(String resourceName, String fieldName,
                                   Object fieldValue) {
    super(String.format("%s not found with %s : %s", resourceName, fieldName, fieldValue));
    this.httpStatus = HttpStatus.NOT_FOUND;
  }
}
