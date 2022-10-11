package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when resource is not found.
 */
@Getter
@Setter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  private String resourceName;
  private String fieldName;
  private Object fieldValue;

  /**
   * Constructor.
   *
   * @param resourceName resource name
   * @param fieldName    field name
   * @param fieldValue   field value
   */
  public ResourceNotFoundException(String resourceName, String fieldName,
                                   Object fieldValue) {
    super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    this.resourceName = resourceName;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
