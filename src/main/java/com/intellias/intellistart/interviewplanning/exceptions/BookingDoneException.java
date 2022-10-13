package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Exception when candidate tries to update booked candidate slot.
 */
@Getter
@Setter
public class BookingDoneException extends AbstractResourceRuntimeException {

  /**
   * Constructor.
   *
   * @param resourceName resource name
   * @param fieldName    name of field
   * @param fieldValue   value of field
   */
  public BookingDoneException(String resourceName, String fieldName,
                              Object fieldValue) {
    super(String.format("Booking is done for %s with %s : %s", resourceName, fieldName,
        fieldValue));
    this.httpStatus = HttpStatus.FORBIDDEN;
  }
}