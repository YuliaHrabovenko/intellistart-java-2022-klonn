package interviewplanning.utils;

import interviewplanning.exceptions.ValidationException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
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
   * Get the first date of a week by a year and a week number.
   *
   * @param year    a year
   * @param weekNum a week number
   * @return the first date of a week
   */
  public static LocalDate getFirstDateOfWeekByYearWeekNum(int year, int weekNum) {
    return LocalDate.ofYearDay(year, 50)
        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNum)
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
  }

  /**
   * Validate if a week number is the next week number.
   *
   * @param weekNum     week number
   * @param nextWeekNum next week number
   */
  public static void validateIsNextWeekNumber(String weekNum, String nextWeekNum) {
    if (!weekNum.equals(nextWeekNum)) {
      throw new ValidationException(ValidationException.NOT_NEXT_WEEK_NUMBER);
    }
  }

  /**
   * Validate if a week number is the current or next week number.
   *
   * @param weekNum week number
   */
  public static void validateIsCurrentOrNextWeekNumber(String weekNum) {
    if (!(weekNum.equals(getCurrentWeekNumber()) || weekNum.equals(getNextWeekNumber()))) {
      throw new ValidationException(ValidationException.NOT_CURRENT_OR_NEXT_WEEK);
    }
  }

  /**
   * Validate Day of week.
   *
   * @param dayOfWeek day of the week
   */
  public static void validateDayOfWeek(DayOfWeek dayOfWeek) {
    if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
      throw new ValidationException(ValidationException.NOT_WORKING_DAY_OF_WEEK);
    }
  }
}
