package com.intellias.intellistart.interviewplanning.utils;

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
}
