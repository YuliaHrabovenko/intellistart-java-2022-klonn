package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.Week;
import com.intellias.intellistart.interviewplanning.repositories.WeekRepository;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import org.springframework.stereotype.Service;

/**
 * Week business logic.
 */
@Service
public class WeekService {
  private final WeekRepository weekRepository;

  public WeekService(WeekRepository weekRepository) {
    this.weekRepository = weekRepository;
  }

  /**
   * Get current week number.
   *
   * @return week object.
   */
  public Week getCurrentWeekNumber() {
    // find current year
    Integer currentYear = LocalDate.now().getYear();

    // find current week number from the start of the year
    LocalDate date = LocalDate.now();
    TemporalField woy = WeekFields.ISO.weekOfWeekBasedYear();
    int weekNumber = date.get(woy);

    String yearAndWeekNum = currentYear + "" + weekNumber;
    Week week = new Week();
    week.setWeekNumber(yearAndWeekNum);
    return week;
  }

  /**
   * Get next week number.
   *
   * @return Week object.
   */
  public Week getNextWeekNumber() {
    Week currentWeek = getCurrentWeekNumber();
    String nextWeekNum = Integer.parseInt(currentWeek.getWeekNumber()) + 1 + "";
    Week nextWeek = new Week();
    nextWeek.setWeekNumber(nextWeekNum);
    return nextWeek;
  }
}
