package com.intellias.intellistart.interviewplanning.utils;

import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;

/**
 * Util class for week.
 */
public final class WeekUtil {

  private WeekUtil() {
  }

  /**
   * Get current week number.
   *
   * @return week object.
   */

  public static String getCurrentWeekNumber() {
    // find current year
    Integer currentYear = LocalDate.now().getYear();

    // find current week number from the start of the year
    LocalDate date = LocalDate.now();
    TemporalField woy = WeekFields.ISO.weekOfWeekBasedYear();
    int weekNumber = date.get(woy);
    return currentYear + "" + weekNumber;
  }

  /**
   * Get next week number.
   *
   * @return Week object.
   */
  public static String getNextWeekNumber() {
    String currentWeek = getCurrentWeekNumber();
    return Integer.parseInt(currentWeek) + 1 + "";
  }

  /**
   * Validate if a week number is the next week number.
   *
   * @param weekNum     week number
   * @param nextWeekNum next week number
   */
  public static void validateIsNextWeekNumber(String weekNum, String nextWeekNum) {
    if (!weekNum.equals(nextWeekNum)) {
      throw new ValidationException(
          ExceptionMessage.NOT_NEXT_WEEK_NUMBER.getMessage());
    }
  }
}
