package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when candidate tries to update booked candidate slot.
 */
@Getter
@Setter
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class BookingDoneException extends RuntimeException {
  private String resourceName;
  private String fieldName;
  private Object fieldValue;

  /**
   * Constructor.
   *
   * @param resourceName resource name
   * @param fieldName    name of field
   * @param fieldValue   value of field
   */
  public BookingDoneException(String resourceName, String fieldName,
                              Object fieldValue) {
    super(String.format("Booking is done for %s with %s : '%s'", resourceName, fieldName,
        fieldValue));
    this.resourceName = resourceName;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}