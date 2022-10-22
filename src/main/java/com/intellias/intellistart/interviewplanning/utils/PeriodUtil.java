package com.intellias.intellistart.interviewplanning.utils;

import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Util class to validate period.
 */
public final class PeriodUtil {
  private PeriodUtil() {
  }

  /**
   * Validate slot period.
   *
   * @param from start time
   * @param to   end time
   */
  public static void validatePeriod(LocalTime from, LocalTime to) {
    if (to.isBefore(from)) {
      throw new ValidationException(ExceptionMessage.START_TIME_BIGGER_THAN_END_TIME.getMessage());
    }

    if (from.getMinute() % 30 != 0 || to.getMinute() % 30 != 0) {
      throw new ValidationException(ExceptionMessage.SLOT_BOUNDARIES_NOT_ROUNDED.getMessage());
    }

    if (Math.abs(Duration.between(from, to).toMinutes()) < 90) {
      throw new ValidationException(ExceptionMessage.PERIOD_DURATION_IS_NOT_ENOUGH.getMessage());
    }
  }

  /**
   * Validate candidate slot date.
   *
   * @param date slot date
   */
  public static void validateDate(LocalDate date) {
    if (date.isBefore(LocalDate.now())) {
      throw new ValidationException(ExceptionMessage.DATE_IS_OUTDATED.getMessage());
    }
  }
}
